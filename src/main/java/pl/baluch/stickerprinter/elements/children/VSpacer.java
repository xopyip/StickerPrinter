package pl.baluch.stickerprinter.elements.children;

import com.google.gson.JsonObject;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import pl.baluch.stickerprinter.data.DrawContext;
import pl.baluch.stickerprinter.elements.ContainerStickerElement;
import pl.baluch.stickerprinter.elements.StickerElement;
import pl.baluch.stickerprinter.elements.StickerElementTypes;

public class VSpacer extends StickerElement<Region> {

    public VSpacer() {
        super(Region::new);
        this.disableResizing();
    }

    @Override
    public void draw(Pane pane, DrawContext drawContext, ContainerStickerElement<?> parent) {
        Region node = nodeSupplier.get();
        VBox.setVgrow(node, Priority.ALWAYS);
        super.setupNode(pane, node, drawContext, parent);
        pane.getChildren().add(node);
    }

    @Override
    public StickerElementTypes getType() {
        return StickerElementTypes.V_SPACER;
    }

    @Override
    public void deserialize(JsonObject properties) {

    }

    @Override
    public JsonObject serialize() {
        return null;
    }
}
