package pl.baluch.stickerprinter.elements;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class HSpacer extends StickerElement {

    public HSpacer() {
        super();
        this.disableResizing();
    }

    @Override
    public void draw(Pane pane) {
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        super.setupNode(pane, region);
        pane.getChildren().add(region);
    }
}
