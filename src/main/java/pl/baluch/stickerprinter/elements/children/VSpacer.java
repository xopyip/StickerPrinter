package pl.baluch.stickerprinter.elements.children;

import com.google.gson.JsonObject;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import pl.baluch.stickerprinter.data.DrawContext;
import pl.baluch.stickerprinter.elements.StickerElement;

public class VSpacer extends StickerElement<Region> {

    public VSpacer() {
        super(Region::new);
        this.disableResizing();
    }

    @Override
    public void draw(Pane pane, DrawContext drawContext) {
        Region node = nodeSupplier.get();
        VBox.setVgrow(node, Priority.ALWAYS);
        super.setupNode(pane, node, drawContext);
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
