package pl.baluch.stickerprinter.elements.containers;

import com.google.gson.JsonObject;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import pl.baluch.stickerprinter.data.DropZone;
import pl.baluch.stickerprinter.elements.ContainerStickerElement;
import pl.baluch.stickerprinter.elements.StickerElement;

public class HBox extends ContainerStickerElement<javafx.scene.layout.HBox> {

    public HBox() {
        super(new javafx.scene.layout.HBox());
        addBoundaryListener((observable, oldValue, newValue) -> updateBoundary());
        setWidth(50);
        setHeight(30);
        addHeightListener((observable, oldValue, newValue) -> children.forEach(child -> child.setHeight(newValue.doubleValue())));
    }

    protected void updateBoundary() {
        dropZones.clear();
        dropZones.add(new DropZone(this, getX(), getY(), getWidth(), getHeight()));
    }

    @Override
    public void addChild(StickerElement<? extends Node> o) {
        o.setHeight(height.get());
        o.setX(children.stream().mapToDouble(StickerElement::getWidth).sum());
        o.addWidthListener((observable, oldValue, newValue) -> {
            double posX = 0;
            for (StickerElement<? extends Node> child : children) {
                child.setX(posX);
                posX += child.getWidth();
            }
        });
        super.addChild(o);
        updateBoundary();
    }

    @Override
    public void setHeight(double height) {
        super.setHeight(height);
        children.forEach(child -> child.setHeight(height));
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
