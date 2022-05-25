package pl.baluch.stickerprinter.windows.main;

import com.google.common.eventbus.Subscribe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import pl.baluch.stickerprinter.AppMain;
import pl.baluch.stickerprinter.Utils;
import pl.baluch.stickerprinter.data.StatusBar;
import pl.baluch.stickerprinter.events.plugin.PluginLoadEvent;
import pl.baluch.stickerprinter.events.plugin.PluginUnloadEvent;
import pl.baluch.stickerprinter.events.UpdateStatusBarEvent;
import pl.baluch.stickerprinter.events.ForceUpdateStickerEvent;
import pl.baluch.stickerprinter.plugins.Item;
import pl.baluch.stickerprinter.plugins.PluginManager;

import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class ItemListController implements Initializable {
    @FXML
    private ListView<Item> itemsList;
    @FXML
    private TextField searchItemField;
    @FXML
    private ChoiceBox<String> itemCategoryChoice;
    private ObservableList<String> categories;
    private ObservableList<Item> items;
    private ResourceBundle resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        //Load categories
        categories = FXCollections.observableList(PluginManager.getInstance().getCategories());
        itemCategoryChoice.setItems(categories);
        itemCategoryChoice.getSelectionModel().selectFirst();

        //Load items
        items = FXCollections.observableList(PluginManager.getInstance().getItems(itemCategoryChoice.getValue()));
        FilteredList<Item> itemFilteredList = new FilteredList<>(items, s -> true);
        itemsList.setItems(itemFilteredList);
        AppMain.EVENT_BUS.post(new UpdateStatusBarEvent(StatusBar.LEFT, String.format(resources.getString("status.left.format"), items.size())));

        //update items on category change
        itemCategoryChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
            items.clear();
            items.addAll(PluginManager.getInstance().getItems(newValue));
        });

        //update items on search text change
        searchItemField.textProperty().addListener((observable, oldValue, newValue) -> {
            itemFilteredList.setPredicate(s -> s.toString().toLowerCase(Locale.ROOT).contains(newValue.toLowerCase(Locale.ROOT))); //todo: better searching
        });
        itemsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> AppMain.EVENT_BUS.post(new ForceUpdateStickerEvent()));
        AppMain.EVENT_BUS.register(this);
    }

    public Optional<Item> getItem() {
        return Optional.ofNullable(itemsList.getSelectionModel().getSelectedItem());
    }

    @Subscribe
    public void onPluginLoad(PluginLoadEvent event) {
        pluginListChanged();
    }

    @Subscribe
    public void onPluginLoad(PluginUnloadEvent event) {
        pluginListChanged();
    }

    private void pluginListChanged() { //todo refactor after implementing plugin cache
        //Refresh items and categories after new plugin load or unload
        Utils.migrateLists(categories, PluginManager.getInstance().getCategories());

        Utils.migrateLists(items, PluginManager.getInstance().getItems(itemCategoryChoice.getValue()));
        AppMain.EVENT_BUS.post(new UpdateStatusBarEvent(StatusBar.LEFT, String.format(resources.getString("status.left.format"), items.size())));
    }
}
