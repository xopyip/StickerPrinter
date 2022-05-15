package pl.baluch.stickerprinter.elements.children;

import com.google.gson.JsonObject;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import pl.baluch.stickerprinter.elements.StickerElement;

public class VSpacer extends StickerElement<Region> {

    public VSpacer() {
        super(new Region());
        this.disableResizing();
        VBox.setVgrow(node, Priority.ALWAYS);
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
