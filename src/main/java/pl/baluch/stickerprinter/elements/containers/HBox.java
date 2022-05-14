package pl.baluch.stickerprinter.elements.containers;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import pl.baluch.stickerprinter.data.DropZone;
import pl.baluch.stickerprinter.elements.ContainerStickerElement;
import pl.baluch.stickerprinter.elements.StickerElement;

import java.util.Set;

public class HBox extends ContainerStickerElement<javafx.scene.layout.HBox> {

    public HBox() {
        super(new javafx.scene.layout.HBox());
        this.width = 20;
        this.height = 20;
        updateBoundary();
    }

    @Override
    protected void updateBoundary() {
        dropZones.clear();
        dropZones.add(new DropZone(this, x, y, width, height));
    }

    @Override
    public Set<DropZone> getDropZones() {
        for (StickerElement<?> child : this.children) {
            System.out.println(child.getWidth());
        }
        return super.getDropZones();
    }

    @Override
    public void addChild(StickerElement<Node> o) {
        o.setHeight(height);
        o.setX(children.stream().mapToDouble(StickerElement::getWidth).sum());
        super.addChild(o);
        updateBoundary();
    }

    @Override
    public void setHeight(double height) {
        super.setHeight(height);
        children.forEach(child -> child.setHeight(height));
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
