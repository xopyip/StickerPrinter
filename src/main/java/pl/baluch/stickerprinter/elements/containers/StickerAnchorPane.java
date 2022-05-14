package pl.baluch.stickerprinter.elements.containers;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import pl.baluch.stickerprinter.data.DropZone;
import pl.baluch.stickerprinter.elements.ContainerStickerElement;

public class StickerAnchorPane extends ContainerStickerElement<AnchorPane> {
    public StickerAnchorPane() {
        super(new AnchorPane(), 0, 0, 100, 100);
        disableResizing();
        disableDragging();
    }

    @Override
    protected void updateBoundary() {
        dropZones.clear();
        dropZones.add(new DropZone(this, x, y, width, height));
    }

    @Override
    public void draw(Pane pane) {
        node.getChildren().clear();
        setupPositionAndSize(node);
        children.forEach(child -> child.draw(node));
        super.setupNode(pane, node);
        pane.getChildren().add(node);
    }
}
