package pl.baluch.stickerprinter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AppMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/main.fxml"));
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
}
