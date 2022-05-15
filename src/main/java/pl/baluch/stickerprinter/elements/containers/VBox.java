package pl.baluch.stickerprinter.elements.containers;

import com.google.gson.JsonObject;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import pl.baluch.stickerprinter.data.DropZone;
import pl.baluch.stickerprinter.elements.ContainerStickerElement;
import pl.baluch.stickerprinter.elements.StickerElement;

public class VBox extends ContainerStickerElement<javafx.scene.layout.VBox> {

    public VBox() {
        super(new javafx.scene.layout.VBox());
        addBoundaryListener((observable, oldValue, newValue) -> updateBoundary());
        setWidth(50);
        setHeight(30);
        addWidthListener((observable, oldValue, newValue) -> children.forEach(child -> child.setWidth(newValue.doubleValue())));
    }

    protected void updateBoundary() {
        dropZones.clear();
        dropZones.add(new DropZone(this, getX(), getY(), getWidth(), getHeight()));
    }

    @Override
    public void addChild(StickerElement<? extends Node> o) {
        o.setWidth(getWidth());
        o.setY(children.stream().mapToDouble(StickerElement::getHeight).sum());
        o.addHeightListener((observable, oldValue, newValue) -> {
            double posY = 0;
            for (StickerElement<? extends Node> child : children) {
                child.setY(posY);
                posY += child.getHeight();
            }
        });
        super.addChild(o);
        updateBoundary();
    }

    @Override
    public void deserialize(JsonObject properties) {

    }

    @Override
    public JsonObject serialize() {
        return null;
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
