package pl.baluch.stickerprinter.elements;

import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public class VSpacer extends StickerElement {
    @Override
    public void draw(Pane pane) {
        Region region = new Region();
        super.setupNode(pane, this, region);
        pane.getChildren().add(region);
    }
}
