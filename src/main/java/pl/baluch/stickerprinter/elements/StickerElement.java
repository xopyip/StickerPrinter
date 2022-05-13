package pl.baluch.stickerprinter.elements;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.function.Supplier;

public abstract class StickerElement {
    protected double x = 0;
    protected double y = 0;
    protected double width = -1;
    protected double height = -1;

    public abstract void draw(Pane pane);

    protected void setupNode(Pane pane, StickerElement stickerElement, Node node) {
        boolean isDraggable = pane instanceof AnchorPane;
        boolean isResizable = node instanceof Region;
        if(isResizable){
            ((Region) node).setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.DOTTED, null, BorderStroke.THIN)));
        }
        //todo: divide into listeners and simply change listener
        node.setOnMouseEntered(event -> {
            if (isDraggable) {
                pane.setCursor(Cursor.MOVE);
            }
        });
        node.setOnMouseExited(event -> pane.setCursor(Cursor.DEFAULT));
        node.setOnMouseMoved(event -> {
            if (isResizable) {
                double cursorX = event.getX();
                double cursorY = event.getY();
                boolean left = cursorX <= 1;
                boolean right = cursorX >= ((Region) node).getPrefWidth() - 1;
                boolean top = cursorY <= 1;
                boolean bottom = cursorY >= ((Region) node).getPrefHeight() - 1;

                if (left && top) pane.setCursor(Cursor.NW_RESIZE);
                else if (right && top) pane.setCursor(Cursor.NE_RESIZE);
                else if (right && bottom) pane.setCursor(Cursor.SE_RESIZE);
                else if (left && bottom) pane.setCursor(Cursor.SW_RESIZE);
                else if (left) pane.setCursor(Cursor.W_RESIZE);
                else if (bottom) pane.setCursor(Cursor.S_RESIZE);
                else if (top) pane.setCursor(Cursor.N_RESIZE);
                else if (right) pane.setCursor(Cursor.E_RESIZE);
                else if (pane.getCursor().toString().endsWith("_RESIZE")) pane.setCursor(Cursor.DEFAULT);
            }
            if (isDraggable && pane.getCursor() == Cursor.DEFAULT && pane.contains(event.getX(), event.getY())) {
                pane.setCursor(Cursor.MOVE);
            }
        });

        SimpleObjectProperty<Point2D> startLocation = new SimpleObjectProperty<>();
        SimpleObjectProperty<Cursor> resizeDirection = new SimpleObjectProperty<>();

        node.setOnMousePressed(event -> {
            if (pane.getCursor() != Cursor.DEFAULT && resizeDirection.get() == null && pane.getCursor() != Cursor.DEFAULT) {
                resizeDirection.set(pane.getCursor());
            }
        });


        //setup drag source
        node.setOnMouseDragged(event -> {
            if (isDraggable && resizeDirection.get() == Cursor.MOVE) {
                double x = event.getX();
                double y = event.getY();
                Point2D point2D = startLocation.get();
                if (point2D == null) {
                    startLocation.setValue(point2D = new Point2D(event.getX(), event.getY()));
                }
                x -= point2D.getX();
                y -= point2D.getY();
                if (pane.contains(node.getLayoutX() + x, node.getLayoutY() + y)) {
                    stickerElement.setX(x + node.getLayoutX());
                    stickerElement.setY(y + node.getLayoutY());
                    node.setLayoutX(stickerElement.getX());
                    node.setLayoutY(stickerElement.getY());
                }
            }
            if (isResizable) {
                Cursor cursor = resizeDirection.get();
                if (cursor != null) {
                    Point2D point2D = startLocation.get();
                    if (point2D == null) {
                        startLocation.setValue(point2D = new Point2D(event.getScreenX(), event.getScreenY()));
                    }
                    Region region = (Region) node;
                    boolean resizeTop = cursor == Cursor.N_RESIZE || cursor == Cursor.NW_RESIZE || cursor == Cursor.NE_RESIZE;
                    boolean resizeBottom = cursor == Cursor.S_RESIZE || cursor == Cursor.SW_RESIZE || cursor == Cursor.SE_RESIZE;
                    boolean resizeLeft = cursor == Cursor.W_RESIZE || cursor == Cursor.NW_RESIZE || cursor == Cursor.SW_RESIZE;
                    boolean resizeRight = cursor == Cursor.E_RESIZE || cursor == Cursor.NE_RESIZE || cursor == Cursor.SE_RESIZE;

                    double newX = stickerElement.getX();
                    double newY = stickerElement.getY();
                    double newWidth = stickerElement.getWidth();
                    double newHeight = stickerElement.getHeight();
                    double dx = event.getScreenX() - point2D.getX();
                    double dy = event.getScreenY() - point2D.getY();
                    if (resizeTop) {
                        newY += dy;
                        newHeight -= dy;
                    }
                    if (resizeLeft) {
                        newX += dx;
                        newWidth -= dx;
                    }
                    if (resizeRight) {
                        newWidth += dx;
                    }
                    if (resizeBottom) {
                        newHeight += dy;
                    }
                    region.setLayoutX(newX);
                    region.setLayoutY(newY);
                    region.setPrefWidth(newWidth);
                    region.setPrefHeight(newHeight);
                }
            }
        });
        node.setOnMouseReleased(event -> {
            startLocation.setValue(null);
            if (resizeDirection.get() != null && isResizable) {

                stickerElement.setX(node.getLayoutX());
                stickerElement.setY(node.getLayoutY());
                stickerElement.setWidth(((Region) node).getPrefWidth());
                stickerElement.setHeight(((Region) node).getPrefHeight());
                resizeDirection.setValue(null);
            }
        });

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

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    protected void setPositionAndSize(Node node) {
        if (x >= 0) {
            node.setLayoutX(x);
        }
        if (y >= 0) {
            node.setLayoutY(y);
        }
        if (node instanceof Region region) {
            if (width >= 0) {
                region.setPrefWidth(width);
            }
            if (height >= 0) {
                region.setPrefHeight(height);
            }
        }
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
