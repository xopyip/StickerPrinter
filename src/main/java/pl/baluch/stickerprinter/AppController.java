package pl.baluch.stickerprinter;

import javafx.application.Application;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AppController implements Initializable {
    @FXML
    public Menu languageSelector;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (Language value : Language.values()) {
            MenuItem item = new MenuItem(value.getTitle());
            item.setOnAction(event -> changeLanguage(value));
            languageSelector.getItems().add(item);
        }
    }

    private void changeLanguage(Language value) {
        ResourceBundle resourceBundle = Storage.getResourceBundle();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(resourceBundle.getString("LanguageChangeTitle"));
        alert.setHeaderText(resourceBundle.getString("LanguageChangeHeader"));
        alert.setContentText(resourceBundle.getString("LanguageChangeText"));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            Storage.getConfig().setLocale(value);
            Storage.saveConfig();
            AppMain.getStage().close();
        }

    }
}
