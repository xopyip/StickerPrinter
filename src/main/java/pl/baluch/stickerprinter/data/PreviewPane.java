package pl.baluch.stickerprinter.data;

import javafx.scene.layout.AnchorPane;

public class PreviewPane extends AnchorPane {
    public PreviewPane(double x, double y, double width, double height) {
        setLayoutX(x);
        setLayoutY(y);
        setPrefSize(width, height);
        setMaxSize(width, height);
        setStyle("-fx-border-color: black; -fx-border-width: 1px;");
    }
}
