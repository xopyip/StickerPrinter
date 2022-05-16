package pl.baluch.stickerprinter;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import pl.baluch.stickerprinter.data.Language;
import pl.baluch.stickerprinter.data.PageStyle;
import pl.baluch.stickerprinter.data.StickerDesign;
import pl.baluch.stickerprinter.data.StickerProperty;
import pl.baluch.stickerprinter.plugins.Item;
import pl.baluch.stickerprinter.plugins.Plugin;
import pl.baluch.stickerprinter.plugins.PluginManager;
import pl.baluch.stickerprinter.windows.PageStyleWindow;
import pl.baluch.stickerprinter.windows.StickerEditorWindow;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class AppController implements Initializable {
    @FXML
    private Menu languageSelector;
    @FXML
    private ChoiceBox<PageStyle> pageStyle;
    @FXML
    private Pane previewPane;
    @FXML
    private Button deletePageStyleButton;
    @FXML
    private Menu pluginsMenu;
    @FXML
    private MenuItem pluginLoadMenu;
    @FXML
    private TextField searchItemField;
    @FXML
    private ListView<Item> itemsList;
    @FXML
    private Label leftStatus;
    @FXML
    private Label cellRatioLabel;
    @FXML
    private ChoiceBox<String> itemCategoryChoice;
    @FXML
    private Pane stickerPreviewPane;
    @FXML
    private TableView<StickerProperty> stickerPreviewDataTable;
    @FXML
    private TableColumn<StickerProperty, String> stickerPreviewKeyColumn;
    @FXML
    private TableColumn<StickerProperty, String> stickerPreviewValueColumn;
    @FXML
    private Text stickerPreviewPropertyText;
    @FXML
    private Label stickerPreviewItemTypeText;
    @FXML
    private Button stickerDesignEdit;

    private SimpleObjectProperty<StickerDesign> stickerDesign = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
        setupStickerPreview(resourceBundle);
        setupItemsList(resourceBundle);
        setupPageStyles(resourceBundle);
        setupMenu(resourceBundle);
    }

    private void setupStickerPreview(ResourceBundle resourceBundle) {
        itemsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> stickerDesign.set(getStickerDesign().orElse(null)));
        pageStyle.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> stickerDesign.set(getStickerDesign().orElse(null)));
        stickerDesign.addListener((observable, oldValue, newValue) -> updateStickerPreview(resourceBundle));

        stickerPreviewKeyColumn.setCellValueFactory(new PropertyValueFactory<>("key"));
        stickerPreviewValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        stickerPreviewDataTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> stickerPreviewPropertyText.setText(newValue.getKey() + ": " + newValue.getValue()));
        stickerDesignEdit.setOnMouseClicked(event -> {
            StickerEditorWindow editorWindow = new StickerEditorWindow(itemsList.getSelectionModel().getSelectedItem(), pageStyle.getSelectionModel().getSelectedItem());
            editorWindow.showAndWait();
            updateStickerPreview(resourceBundle);
        });
    }

    /**
     * Setups left column with items view
     */
    private void setupItemsList(ResourceBundle resourceBundle) {
        //Load categories
        ObservableList<String> categories = FXCollections.observableList(PluginManager.getInstance().getCategories());
        itemCategoryChoice.setItems(categories);
        itemCategoryChoice.getSelectionModel().selectFirst();

        //Load items
        ObservableList<Item> data = FXCollections.observableList(PluginManager.getInstance().getItems(itemCategoryChoice.getValue()));
        FilteredList<Item> itemFilteredList = new FilteredList<>(data, s -> true);
        itemsList.setItems(itemFilteredList);
        leftStatus.setText(String.format(resourceBundle.getString("status.left.format"), data.size()));

        PluginManager.getInstance().addChangeListener((event) -> {
            //Refresh items and categories after new plugin load or unload
            Utils.migrateLists(categories, PluginManager.getInstance().getCategories());

            List<Item> items = PluginManager.getInstance().getItems(itemCategoryChoice.getValue());
            Utils.migrateLists(data, items);
            leftStatus.setText(String.format(resourceBundle.getString("status.left.format"), items.size()));
        });

        //update items on category change
        itemCategoryChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
            List<Item> items = PluginManager.getInstance().getItems(newValue);
            data.clear();
            data.addAll(items);
        });

        //update items on search text change
        searchItemField.textProperty().addListener((observable, oldValue, newValue) -> {
            itemFilteredList.setPredicate(s -> s.toString().toLowerCase(Locale.ROOT).contains(newValue.toLowerCase(Locale.ROOT))); //todo: better searching
        });
    }

    private void updateStickerPreview(ResourceBundle resourceBundle) {
        Optional<Item> selectedItem = getItem();

        stickerPreviewDataTable.getItems().clear();
        stickerPreviewItemTypeText.setText("");
        stickerDesignEdit.setVisible(selectedItem.isPresent());

        StickerDesign currentStickerDesign = stickerDesign.get();
        stickerDesignEdit.setText(currentStickerDesign != null ? resourceBundle.getString("sticker.preview.edit") : resourceBundle.getString("sticker.preview.create"));
        stickerPreviewPane.getChildren().clear();
        if (currentStickerDesign != null && selectedItem.isPresent()) {
            Item item = selectedItem.get();
            stickerPreviewDataTable.getItems().addAll(item.getPreviewProperties());
            stickerPreviewItemTypeText.setText(String.format(resourceBundle.getString("sticker.preview.itemtype"), item.getTypeName()));
            currentStickerDesign.renderPreview(stickerPreviewPane, selectedItem.get());
        } else {
            TextFlow textFlow = new TextFlow(new Text(resourceBundle.getString("sticker.preview.no_design")));
            textFlow.setLayoutX(10);
            textFlow.setLayoutY(10);
            textFlow.setPrefWidth(stickerPreviewPane.getWidth() - 20);
            stickerPreviewPane.getChildren().add(textFlow);
        }
    }

    private Optional<StickerDesign> getStickerDesign() {
        Optional<PageStyle> pageStyle = getPageStyle();
        Optional<Item> item = getItem();
        if (pageStyle.isEmpty() || item.isEmpty()) {
            return Optional.empty();
        }
        return Storage.getStickerDesign(pageStyle.get().getCellRatio(), item.get());
    }

    private Optional<PageStyle> getPageStyle() {
        return Optional.ofNullable(pageStyle.getSelectionModel().getSelectedItem());
    }

    private Optional<Item> getItem() {
        return Optional.ofNullable(itemsList.getSelectionModel().getSelectedItem());
    }

    /**
     * Setups menu, submenus and their handlers
     */
    private void setupMenu(ResourceBundle resourceBundle) {
        for (Plugin plugin : PluginManager.getInstance().getPlugins()) {
            addPluginMenu(plugin, resourceBundle);
        }
        pluginLoadMenu.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(resourceBundle.getString("plugins.chooser.title"));
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter(resourceBundle.getString("plugins.extension.name"), ".jar"));
            File file = fileChooser.showOpenDialog(AppMain.getStage().getOwner());
            if (file == null) {
                return;
            }
            try {
                PluginManager.getInstance().loadExternal(file).ifPresent(item -> addPluginMenu(item, resourceBundle));
            } catch (IOException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        for (Language value : Language.values()) {
            MenuItem item = new MenuItem(value.getTitle());
            item.setOnAction(event -> changeLanguage(value, resourceBundle));
            languageSelector.getItems().add(item);
        }
    }

    /**
     * Add plugin to the menu with their submenus
     *
     * @param plugin - plugin to be added
     */
    private void addPluginMenu(Plugin plugin, ResourceBundle resourceBundle) {
        MenuItem unloadPlugin = new MenuItem(resourceBundle.getString("menu.plugins.unload"));
        unloadPlugin.setOnAction(event -> {
            try {
                PluginManager.getInstance().unload(plugin);
                pluginsMenu.getItems().removeIf(item -> Objects.equals(item.getText(), plugin.getName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Menu menuItem = new Menu(plugin.getName(), null, unloadPlugin);
        pluginsMenu.getItems().add(0, menuItem);
    }

    /**
     * Setups right column with page styles
     */
    private void setupPageStyles(ResourceBundle resourceBundle) {
        //initialize page styles
        pageStyle.getItems().addAll(Storage.getConfig().getPageStyles());
        pageStyle.getSelectionModel().selectFirst();
        pageStyle.getItems().add(new PageStyle.New());

        //draw preview
        Platform.runLater(() -> {
            pageStyle.getSelectionModel().getSelectedItem().drawPreview(previewPane);
            cellRatioLabel.setText(String.format(resourceBundle.getString("pagestyle.ratio"), pageStyle.getSelectionModel().getSelectedItem().getCellRatio()));
        });

        //add change handler
        pageStyle.setOnAction(event -> {
            SingleSelectionModel<PageStyle> selectionModel = pageStyle.getSelectionModel();
            if (selectionModel.getSelectedItem() instanceof PageStyle.New) {
                PageStyleWindow pageStyleWindow = new PageStyleWindow();
                PageStyle pageStyle = pageStyleWindow.createPageStyle();
                if (pageStyle == null) {
                    return;
                }
                this.pageStyle.getItems().add(this.pageStyle.getItems().size() - 1, pageStyle);
                selectionModel.select(pageStyle);
                Storage.getConfig().getPageStyles().add(pageStyle);
                Storage.saveConfig();
            }
            selectionModel.getSelectedItem().drawPreview(previewPane);
            cellRatioLabel.setText(String.format(resourceBundle.getString("pagestyle.ratio"), pageStyle.getSelectionModel().getSelectedItem().getCellRatio()));
        });

        //add delete handler
        deletePageStyleButton.setOnMouseClicked(event -> {
            PageStyle selectedItem = this.pageStyle.getSelectionModel().getSelectedItem();
            pageStyle.getItems().remove(selectedItem);
            Storage.getConfig().getPageStyles().remove(selectedItem);
            pageStyle.getSelectionModel().selectFirst();
            Storage.saveConfig();
        });
    }

    /**
     * Changes language to value and restarts application
     *
     * @param value - target language
     */
    private void changeLanguage(Language value, ResourceBundle resourceBundle) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(resourceBundle.getString("language.title"));
        alert.setHeaderText(resourceBundle.getString("language.header"));
        alert.setContentText(resourceBundle.getString("language.text"));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Storage.getConfig().setLocale(value);
            Storage.saveConfig();
            AppMain.getStage().close();
        }

    }
}
