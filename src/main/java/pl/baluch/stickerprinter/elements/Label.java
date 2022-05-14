package pl.baluch.stickerprinter.elements;

import javafx.scene.layout.Pane;

public class Label extends StickerElement {
    @Override
    public void draw(Pane pane) {
        javafx.scene.control.Label text = new javafx.scene.control.Label("Test");
        text.setWrapText(false);
        text.setMinWidth(text.getPrefWidth());
        super.setupPositionAndSize(text);
        super.setupNode(pane, text);
        pane.getChildren().add(text);
    }
}
