package pl.baluch.stickerprinter.elements;

import javafx.scene.layout.Pane;

public class HBox extends StickerElement{
    public HBox() {
        super();
        this.width = 10;
        this.height = 10;
    }

    @Override
    public void draw(Pane pane) {
        javafx.scene.layout.HBox hBox = new javafx.scene.layout.HBox();
        setPositionAndSize(hBox);
        super.setupNode(pane, this, hBox);
        pane.getChildren().add(hBox);
    }
}
