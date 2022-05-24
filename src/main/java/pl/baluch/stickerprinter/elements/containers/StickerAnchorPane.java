package pl.baluch.stickerprinter.elements.containers;

import com.google.gson.JsonObject;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import pl.baluch.stickerprinter.data.DrawContext;
import pl.baluch.stickerprinter.data.DropZone;
import pl.baluch.stickerprinter.elements.ContainerStickerElement;

public class StickerAnchorPane extends ContainerStickerElement<AnchorPane> {
    public StickerAnchorPane() {
        super(AnchorPane::new, 0, 0, 100, 100);
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
    public void draw(Pane pane, DrawContext drawContext) {
        AnchorPane node = nodeSupplier.get();
        node.getChildren().clear();
        bindBounds(node);
        children.forEach(child -> child.draw(node, drawContext));
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
