package pl.baluch.stickerprinter.data;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DropZone extends Rectangle {
    public DropZone(double x, double y, double width, double height) {
        super(x, y, width, height);
        setFill(Color.TRANSPARENT);
        setStroke(Color.RED);
    }
}
