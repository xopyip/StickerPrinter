package pl.baluch.stickerprinter;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import pl.baluch.stickerprinter.data.PageStyle;
import pl.baluch.stickerprinter.plugins.Item;
import pl.baluch.stickerprinter.plugins.Plugin;
import pl.baluch.stickerprinter.plugins.PluginManager;
import pl.baluch.stickerprinter.windows.PageStyleWindow;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class AppController implements Initializable {
    @FXML
    public Menu languageSelector;
    @FXML
    public ChoiceBox<PageStyle> pageStyle;
    @FXML
    public Pane previewPane;
    @FXML
    public Button deletePageStyleButton;
    @FXML
    public Menu pluginsMenu;
    @FXML
    public MenuItem pluginLoadMenu;
    @FXML
    public TextField searchItemField;
    @FXML
    public ListView<Item> itemsList;
    @FXML
    public Label leftStatus;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupItemsList();
        setupPageStyles();
        setupMenu();
    }

    /**
     * Setups left column with items view
     */
    private void setupItemsList() {
        ResourceBundle resourceBundle = Storage.getResourceBundle();
        ObservableList<Item> data = FXCollections.observableArrayList(PluginManager.getInstance().getItems());
        FilteredList<Item> itemFilteredList = new FilteredList<>(data, s -> true);
        itemsList.setItems(itemFilteredList);
        leftStatus.setText(String.format(resourceBundle.getString("status.left.format"), data.size()));

        PluginManager.getInstance().addObserver((o, arg) -> {
            List<Item> items = PluginManager.getInstance().getItems();
            data.clear();
            data.addAll(items);
            leftStatus.setText(String.format(resourceBundle.getString("status.left.format"), items.size()));
        });

        searchItemField.textProperty().addListener((observable, oldValue, newValue) -> {
            itemFilteredList.setPredicate(s -> s.toString().toLowerCase(Locale.ROOT).contains(newValue.toLowerCase(Locale.ROOT))); //todo: better searching
        });
    }

    /**
     * Setups menu, submenus and their handlers
     */
    private void setupMenu() {
        ResourceBundle resourceBundle = Storage.getResourceBundle();
        for (Plugin plugin : PluginManager.getInstance().getPlugins()) {
            addPluginMenu(plugin);
        }
        pluginLoadMenu.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(resourceBundle.getString("plugins.chooser.title"));
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter(resourceBundle.getString("plugins.extension.name"), ".jar"));
            File file = fileChooser.showOpenDialog(AppMain.getStage().getOwner());
            try {
                PluginManager.getInstance().loadExternal(file).ifPresent(this::addPluginMenu);
            } catch (IOException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        for (Language value : Language.values()) {
            MenuItem item = new MenuItem(value.getTitle());
            item.setOnAction(event -> changeLanguage(value));
            languageSelector.getItems().add(item);
        }
    }

    /**
     * Add plugin to the menu with their submenus
     * @param plugin - plugin to be added
     */
    private void addPluginMenu(Plugin plugin) {
        ResourceBundle resourceBundle = Storage.getResourceBundle();
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
    private void setupPageStyles() {
        //initialize page styles
        pageStyle.getItems().addAll(Storage.getConfig().getPageStyles());
        pageStyle.getSelectionModel().selectFirst();
        pageStyle.getItems().add(new PageStyle.New());

        //draw preview
        Platform.runLater(() -> pageStyle.getSelectionModel().getSelectedItem().drawPreview(previewPane));

        //add change handler
        pageStyle.setOnAction(event -> {
            SingleSelectionModel<PageStyle> selectionModel = pageStyle.getSelectionModel();
            if (selectionModel.getSelectedItem() instanceof PageStyle.New) {
                PageStyleWindow pageStyleWindow = new PageStyleWindow();
                PageStyle pageStyle = pageStyleWindow.createPageStyle();
                if(pageStyle == null){
                    return;
                }
                this.pageStyle.getItems().add(this.pageStyle.getItems().size() - 1, pageStyle);
                selectionModel.select(pageStyle);
                Storage.getConfig().getPageStyles().add(pageStyle);
                Storage.saveConfig();
            }
            selectionModel.getSelectedItem().drawPreview(previewPane);
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
     * @param value - target language
     */
    private void changeLanguage(Language value) {
        ResourceBundle resourceBundle = Storage.getResourceBundle();
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
