package pl.baluch.stickerprinter.elements;

import javafx.scene.layout.Pane;
import pl.baluch.stickerprinter.data.DropZone;

public class HBox extends ContainerStickerElement {

    public HBox() {
        super();
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
    public void addChild(StickerElement o) {
        o.setHeight(height);
        o.setX(children.stream().mapToDouble(s -> s.width).sum());
        super.addChild(o);
    }

    @Override
    public void setHeight(double height) {
        super.setHeight(height);
        children.forEach(child -> child.setHeight(height));
    }

    @Override
    public void draw(Pane pane) {
        javafx.scene.layout.HBox hBox = new javafx.scene.layout.HBox();
        setupPositionAndSize(hBox);
        children.forEach(child -> child.draw(hBox));
        super.setupNode(pane, this, hBox);
        pane.getChildren().add(hBox);
    }
}
