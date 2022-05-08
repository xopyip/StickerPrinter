package pl.baluch.stickerprinter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.baluch.stickerprinter.plugins.PluginManager;

import java.io.IOException;

public class AppMain extends Application {

    private static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        try {
            PluginManager.getInstance().load();
        } catch (IOException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        stage.setOnCloseRequest(event -> PluginManager.getInstance().onClose());
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/main.fxml"));

        fxmlLoader.setResources(Storage.getResourceBundle());

        Parent pane = null;
        try {
            pane = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert pane != null;
        Scene scene = new Scene(pane);

        primaryStage.setTitle("StickerPrinter");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static Stage getStage() {
        return stage;
    }
}
