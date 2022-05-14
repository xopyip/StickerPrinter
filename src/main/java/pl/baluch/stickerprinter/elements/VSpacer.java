package pl.baluch.stickerprinter.elements;

import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class VSpacer extends StickerElement {

    public VSpacer() {
        super();
        this.disableResizing();
    }

    @Override
    public void draw(Pane pane) {
        Region region = new Region();
        VBox.setVgrow(region, Priority.ALWAYS);
        super.setupNode(pane, region);
        pane.getChildren().add(region);
    }
}
