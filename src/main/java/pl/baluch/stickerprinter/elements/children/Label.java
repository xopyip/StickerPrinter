package pl.baluch.stickerprinter.elements.children;

import javafx.scene.layout.Pane;
import pl.baluch.stickerprinter.elements.StickerElement;

public class Label extends StickerElement<javafx.scene.control.Label> {
    public Label() {
        super(new javafx.scene.control.Label("Test"));
        node.setWrapText(false);
    }

    @Override
    public void draw(Pane pane) {
        super.setupPositionAndSize(node);
        super.setupNode(pane, node);
        pane.getChildren().add(node);
    }
}
