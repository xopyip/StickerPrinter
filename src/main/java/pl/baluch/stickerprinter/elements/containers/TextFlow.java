package pl.baluch.stickerprinter.elements.containers;

import com.google.gson.JsonObject;
import javafx.scene.layout.Pane;
import pl.baluch.stickerprinter.data.DrawContext;
import pl.baluch.stickerprinter.data.DropZone;
import pl.baluch.stickerprinter.elements.ContainerStickerElement;

public class TextFlow extends ContainerStickerElement<javafx.scene.text.TextFlow> {
    public TextFlow() {
        super(javafx.scene.text.TextFlow::new);
        setWidth(400);
        setHeight(100);
        addBoundaryListener((observable, oldValue, newValue) -> {
            dropZones.clear();
            dropZones.add(new DropZone(TextFlow.this, TextFlow.this.getX(), TextFlow.this.getY(), TextFlow.this.getWidth(), TextFlow.this.getHeight()));
        });
    }

    @Override
    public void draw(Pane pane, DrawContext drawContext) {
        javafx.scene.text.TextFlow node = nodeSupplier.get();
        node.getChildren().clear();
        setupPositionAndSize(node);
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
