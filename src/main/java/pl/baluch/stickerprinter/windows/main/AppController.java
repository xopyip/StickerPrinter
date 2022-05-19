package pl.baluch.stickerprinter.windows.main;

import com.google.common.eventbus.Subscribe;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import pl.baluch.stickerprinter.AppMain;
import pl.baluch.stickerprinter.Storage;
import pl.baluch.stickerprinter.data.Language;
import pl.baluch.stickerprinter.data.PageStyle;
import pl.baluch.stickerprinter.data.StickerDesign;
import pl.baluch.stickerprinter.events.ForceUpdateStickerEvent;
import pl.baluch.stickerprinter.events.PrintCellClickedEvent;
import pl.baluch.stickerprinter.events.StickerDesignChangedEvent;
import pl.baluch.stickerprinter.plugins.Item;
import pl.baluch.stickerprinter.plugins.Plugin;
import pl.baluch.stickerprinter.plugins.PluginManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class AppController implements Initializable {
    @FXML
    private Menu languageSelector;
    @FXML
    private Menu pluginsMenu;
    @FXML
    private MenuItem pluginLoadMenu;

    //Controllers
    @SuppressWarnings("unused")
    @FXML
    private ItemListController itemListController;
    @SuppressWarnings("unused")
    @FXML
    private StatusBarController statusController;
    @SuppressWarnings("unused")
    @FXML
    private PreviewController previewController;
    @SuppressWarnings("unused")
    @FXML
    private PrintController printController;

    private final SimpleObjectProperty<StickerDesign> stickerDesign = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
        previewController.setAppController(this);
        printController.setAppController(this);
        setupMenu(resourceBundle);
        AppMain.EVENT_BUS.register(this);
        stickerDesign.addListener((observable, oldValue, newValue) -> AppMain.EVENT_BUS.post(new StickerDesignChangedEvent(newValue)));
    }

    public Optional<StickerDesign> matchStickerDesign() {
        Optional<PageStyle> pageStyle = getPageStyle();
        Optional<Item> item = itemListController.getItem();
        if (pageStyle.isEmpty() || item.isEmpty()) {
            return Optional.empty();
        }
        return Storage.matchStickerDesign(pageStyle.get().getCellRatio(), item.get());
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

    // GETTERS AND SETTERS
    public ItemListController getItemListController() {
        return itemListController;
    }


    public StickerDesign getStickerDesign() {
        return stickerDesign.get();
    }

    public Optional<PageStyle> getPageStyle() {
        return Optional.ofNullable(printController.getPageStyle().getSelectionModel().getSelectedItem());
    }

    public void setStickerDesign(StickerDesign stickerDesign) {
        this.stickerDesign.set(stickerDesign);
    }

    // EVENT HANDLERS
    @Subscribe
    public void onStickerUpdateClicked(ForceUpdateStickerEvent event) {
        stickerDesign.set(matchStickerDesign().orElse(null));
    }
    @Subscribe
    public void onCellClicked(PrintCellClickedEvent event) {
        Optional<StickerDesign> design = matchStickerDesign();
        Optional<Item> item = itemListController.getItem();
        if (design.isPresent() && item.isPresent()) {
            event.printCell().setSticker(design.get(), item.get());
        }
    }
}
