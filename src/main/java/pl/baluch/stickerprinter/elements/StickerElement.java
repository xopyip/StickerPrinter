package pl.baluch.stickerprinter.elements;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.util.function.Supplier;

public abstract class StickerElement {
    private static final DataFormat NODE_FORMAT = new DataFormat("node-format");
    protected double x = 0;
    protected double y = 0;
    protected double width = -1;
    protected double height = -1;

    public abstract void draw(Pane pane);

    protected void setupMovable(Pane pane, StickerElement stickerElement, Node node) {
        if (pane instanceof AnchorPane) {
            AnchorPane anchorPane = (AnchorPane) pane;
            node.setOnMouseEntered(event -> anchorPane.setCursor(Cursor.MOVE));
            node.setOnMouseExited(event -> anchorPane.setCursor(Cursor.DEFAULT));
            //setup drag source
            node.setOnDragDetected(event -> {
                Dragboard db = node.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(node.getId());
                db.setContent(content);
                event.consume();
            });
            node.setOnMouseDragged(event -> {
                double x = event.getX();
                double y = event.getY();
                if (anchorPane.contains(node.getLayoutX() + x, node.getLayoutY() + y)) {
                    stickerElement.setX(x + node.getLayoutX());
                    stickerElement.setY(y + node.getLayoutY());
                    node.setLayoutX(stickerElement.getX());
                    node.setLayoutY(stickerElement.getY());
                }
            });
            node.setOnMouseReleased(event -> {
                System.out.println("released");
            });
        }
    }

    private void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public record Provider<T extends StickerElement>(String name, Supplier<T> supplier) {

        public T get() {
            return this.supplier.get();
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
