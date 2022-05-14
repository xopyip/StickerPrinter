package pl.baluch.stickerprinter.data;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import pl.baluch.stickerprinter.elements.ContainerStickerElement;

public class DropZone extends Rectangle {
    private final ContainerStickerElement<?> container;

    public DropZone(ContainerStickerElement<?> container, double x, double y, double width, double height) {
        super(x, y, width, height);
        this.container = container;
        setFill(Color.TRANSPARENT);
        setStroke(Color.RED);
    }

    public ContainerStickerElement<?> getContainer() {
        return container;
    }
}
