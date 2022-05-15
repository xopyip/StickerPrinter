package pl.baluch.stickerprinter.elements.children;

import com.google.gson.JsonObject;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import pl.baluch.stickerprinter.elements.StickerElement;

public class HSpacer extends StickerElement<Region> {

    public HSpacer() {
        super(new Region());
        HBox.setHgrow(node, Priority.ALWAYS);
        this.disableResizing();
    }

    @Override
    public void draw(Pane pane) {
        super.setupNode(pane, node);
        pane.getChildren().add(node);
    }

    @Override
    public void deserialize(JsonObject properties) {

    }

    @Override
    public JsonObject serialize() {
        return null;
    }
}
