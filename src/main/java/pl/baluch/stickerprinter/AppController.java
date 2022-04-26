package pl.baluch.stickerprinter;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import pl.baluch.stickerprinter.data.PageStyle;
import pl.baluch.stickerprinter.windows.PageStyleWindow;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AppController implements Initializable {
    @FXML
    public Menu languageSelector;
    @FXML
    public ChoiceBox<PageStyle> pageStyle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pageStyle.getItems().addAll(Storage.getConfig().getPageStyles());
        pageStyle.getItems().add(new PageStyle.New());
        pageStyle.setOnAction(event -> {
            SingleSelectionModel<PageStyle> selectionModel = pageStyle.getSelectionModel();
            if (selectionModel.getSelectedItem() instanceof PageStyle.New) {
                PageStyleWindow pageStyleWindow = new PageStyleWindow();
                pageStyleWindow.showAndWait();
                PageStyle newStyle = pageStyleWindow.getNewStyle();
                pageStyle.getItems().add(pageStyle.getItems().size() - 1, newStyle);
                selectionModel.select(newStyle);
            }
        });

        for (Language value : Language.values()) {
            MenuItem item = new MenuItem(value.getTitle());
            item.setOnAction(event -> changeLanguage(value));
            languageSelector.getItems().add(item);
        }
    }

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
