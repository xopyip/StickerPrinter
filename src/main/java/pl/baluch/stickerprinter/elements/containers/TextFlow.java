package pl.baluch.stickerprinter.elements.containers;

import com.google.gson.JsonObject;
import javafx.scene.layout.Pane;
import pl.baluch.stickerprinter.data.DropZone;
import pl.baluch.stickerprinter.elements.ContainerStickerElement;

public class TextFlow extends ContainerStickerElement<javafx.scene.text.TextFlow> {
    public TextFlow() {
        super(new javafx.scene.text.TextFlow());
        setWidth(400);
        setHeight(100);
        addBoundaryListener((observable, oldValue, newValue) -> {
            dropZones.clear();
            dropZones.add(new DropZone(TextFlow.this, TextFlow.this.getX(), TextFlow.this.getY(), TextFlow.this.getWidth(), TextFlow.this.getHeight()));
        });
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
