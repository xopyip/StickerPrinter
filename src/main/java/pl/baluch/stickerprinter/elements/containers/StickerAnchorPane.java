package pl.baluch.stickerprinter.elements.containers;

import com.google.gson.JsonObject;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import pl.baluch.stickerprinter.data.DropZone;
import pl.baluch.stickerprinter.elements.ContainerStickerElement;

public class StickerAnchorPane extends ContainerStickerElement<AnchorPane> {
    public StickerAnchorPane() {
        super(new AnchorPane(), 0, 0, 100, 100);
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
    public void draw(Pane pane) {
        node.getChildren().clear();
        setupPositionAndSize(node);
        children.forEach(child -> child.draw(node));
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
