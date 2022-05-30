package pl.baluch.stickerprinter.elements.containers;

import com.google.gson.JsonObject;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import pl.baluch.stickerprinter.data.DrawContext;
import pl.baluch.stickerprinter.data.DropZone;
import pl.baluch.stickerprinter.elements.ContainerStickerElement;
import pl.baluch.stickerprinter.elements.StickerElementTypes;

public class StickerAnchorPane extends ContainerStickerElement<AnchorPane> {
    public StickerAnchorPane() {
        super(0, 0, 100, 100);
        disableResizing();
        disableDragging();
        addBoundaryListener((observable, oldValue, newValue) -> updateBoundary());
        updateBoundary();
    }

    protected void updateBoundary() {
        dropZones.clear();
        dropZones.add(new DropZone(this, getX(), getY(), getWidth(), getHeight()));
    }

    @Override
    public void draw(Pane pane, DrawContext drawContext, ContainerStickerElement<?> parent) {
        AnchorPane node = new AnchorPane();
        node.getChildren().clear();
        bindBounds(node);
        children.forEach(child -> child.draw(node, drawContext, this));
        super.setupNode(pane, node, drawContext, parent);
        pane.getChildren().add(node);
    }

    @Override
    public StickerElementTypes getType() {
        return StickerElementTypes.ANCHOR_PANE;
    }

    @Override
    public void deserialize(JsonObject properties) {

    }

    @Override
    public JsonObject serialize() {
        return null;
    }
}
