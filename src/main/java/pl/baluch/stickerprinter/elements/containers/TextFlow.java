package pl.baluch.stickerprinter.elements.containers;

import com.google.gson.JsonObject;
import javafx.scene.layout.Pane;
import pl.baluch.stickerprinter.data.DrawContext;
import pl.baluch.stickerprinter.data.DropZone;
import pl.baluch.stickerprinter.elements.ContainerStickerElement;
import pl.baluch.stickerprinter.elements.StickerElementTypes;

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
    public void draw(Pane pane, DrawContext drawContext, ContainerStickerElement<?> parent) {
        javafx.scene.text.TextFlow node = nodeSupplier.get();
        node.getChildren().clear();
        bindBounds(node);
        children.forEach(child -> child.draw(node, drawContext, this));
        super.setupNode(pane, node, drawContext, parent);
        pane.getChildren().add(node);
    }

    @Override
    public StickerElementTypes getType() {
        return StickerElementTypes.TEXT_FLOW;
    }

    @Override
    public void deserialize(JsonObject properties) {

    }

    @Override
    public JsonObject serialize() {
        return null;
    }
}
