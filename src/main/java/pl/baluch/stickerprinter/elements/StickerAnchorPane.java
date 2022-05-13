package pl.baluch.stickerprinter.elements;

import javafx.scene.layout.Pane;
import pl.baluch.stickerprinter.data.DropZone;

public class StickerAnchorPane extends ContainerStickerElement {
    @Override
    protected void updateBoundary() {
        dropZones.clear();
        dropZones.add(new DropZone(x, y, width, height));
    }

    @Override
    public void draw(Pane pane) {
        for (StickerElement child : children) {
            child.draw(pane);
        }
    }
}
