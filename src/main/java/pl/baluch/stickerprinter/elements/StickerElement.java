package pl.baluch.stickerprinter.elements;

import com.google.gson.JsonObject;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import pl.baluch.stickerprinter.data.DrawContext;

import java.util.List;
import java.util.Stack;

public abstract class StickerElement<T extends Node> {
    protected SimpleDoubleProperty x = new SimpleDoubleProperty(0);
    protected SimpleDoubleProperty y = new SimpleDoubleProperty(0);
    protected SimpleDoubleProperty width = new SimpleDoubleProperty(-1);
    protected SimpleDoubleProperty height = new SimpleDoubleProperty(-1);
    private boolean resizableDisabled = false;
    private boolean draggingDisabled = false;
    protected transient T node;

    //Stack to trace hover state
    private static final Stack<StickerElement<?>> mouseOverStack = new Stack<>();
    private StickerElementTypes type;

    public StickerElement(T node, int x, int y, int w, int h) {
        this.node = node;
        this.x.set(x);
        this.y.set(y);
        this.width.set(w);
        this.height.set(h);
    }

    public StickerElement(T node) {
        this.node = node;
    }

    public abstract void draw(Pane pane, DrawContext drawContext);

    /**
     * Sets required listeners and properties to make element resiable and draggable
     *
     * @param pane        - parent pane
     * @param node        - node to be resized and dragged
     * @param drawContext - context of drawing
     */
    protected void setupNode(Pane pane, Node node, DrawContext drawContext) {
        boolean isDraggable = (pane instanceof AnchorPane) && !draggingDisabled && drawContext.isEditor();
        boolean isResizable = (node instanceof Region) && !resizableDisabled && drawContext.isEditor();
        if (isResizable) {
            ((Region) node).setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.DOTTED, null, BorderStroke.THIN)));
        }
        //todo: divide into listeners and simply change listener
        node.setOnMouseEntered(event -> {
            if (!mouseOverStack.empty())
                mouseOverStack.peek().setHighlighted(false);
            mouseOverStack.add(this);
            this.setHighlighted(true);
            if (isDraggable) {
                node.setCursor(Cursor.MOVE);
            }
        });
        node.setOnMouseExited(event -> {
            this.setHighlighted(false);
            //removing redundant elements from stack to prevent highlighting of other elements after fast mouse movement
            while (!mouseOverStack.empty() && mouseOverStack.peek() != this) {
                mouseOverStack.peek().setHighlighted(false);
                mouseOverStack.pop();
            }
            if (!mouseOverStack.empty())
                mouseOverStack.pop();
            if (!mouseOverStack.empty())
                mouseOverStack.peek().setHighlighted(true);
            node.setCursor(Cursor.DEFAULT);
        });
        node.setOnMouseMoved(event -> {
            if (isResizable && mouseOverStack.peek() == this) {
                double cursorX = event.getX();
                double cursorY = event.getY();
                boolean left = cursorX <= 1;
                boolean right = cursorX >= ((Region) node).getPrefWidth() - 1;
                boolean top = cursorY <= 1;
                boolean bottom = cursorY >= ((Region) node).getPrefHeight() - 1;

                if (left && top) node.setCursor(Cursor.NW_RESIZE);
                else if (right && top) node.setCursor(Cursor.NE_RESIZE);
                else if (right && bottom) node.setCursor(Cursor.SE_RESIZE);
                else if (left && bottom) node.setCursor(Cursor.SW_RESIZE);
                else if (left) node.setCursor(Cursor.W_RESIZE);
                else if (bottom) node.setCursor(Cursor.S_RESIZE);
                else if (top) node.setCursor(Cursor.N_RESIZE);
                else if (right) node.setCursor(Cursor.E_RESIZE);
                else if (node.getCursor() == null || node.getCursor().toString().endsWith("_RESIZE"))
                    node.setCursor(Cursor.DEFAULT);
            }
            if (isDraggable && node.getCursor() == Cursor.DEFAULT && mouseOverStack.peek() == this) {
                node.setCursor(Cursor.MOVE);
            }
        });

        SimpleObjectProperty<Point2D> startLocation = new SimpleObjectProperty<>();
        SimpleObjectProperty<Cursor> savedCursor = new SimpleObjectProperty<>();

        node.setOnMousePressed(event -> {
            if (node.getCursor() != Cursor.DEFAULT && savedCursor.get() == null && node.getCursor() != Cursor.DEFAULT && mouseOverStack.peek() == this) {
                savedCursor.set(node.getCursor());
            }
        });


        //setup drag source
        node.setOnMouseDragged(event -> {
            if (isDraggable && savedCursor.get() == Cursor.MOVE) {
                double x = event.getX();
                double y = event.getY();
                Point2D point2D = startLocation.get();
                if (point2D == null) {
                    startLocation.setValue(point2D = new Point2D(event.getX(), event.getY()));
                }
                x -= point2D.getX();
                y -= point2D.getY();
                if (pane.contains(node.getLayoutX() + x, node.getLayoutY() + y)) {
                    this.setX(x + node.getLayoutX());
                    this.setY(y + node.getLayoutY());
                    node.setLayoutX(this.getX());
                    node.setLayoutY(this.getY());
                }
            }
            if (isResizable && savedCursor.get() != null) {
                Cursor cursor = savedCursor.get();
                Point2D point2D = startLocation.get();
                if (point2D == null) {
                    startLocation.setValue(point2D = new Point2D(event.getScreenX(), event.getScreenY()));
                }
                Region region = (Region) node;
                boolean resizeTop = cursor == Cursor.N_RESIZE || cursor == Cursor.NW_RESIZE || cursor == Cursor.NE_RESIZE;
                boolean resizeBottom = cursor == Cursor.S_RESIZE || cursor == Cursor.SW_RESIZE || cursor == Cursor.SE_RESIZE;
                boolean resizeLeft = cursor == Cursor.W_RESIZE || cursor == Cursor.NW_RESIZE || cursor == Cursor.SW_RESIZE;
                boolean resizeRight = cursor == Cursor.E_RESIZE || cursor == Cursor.NE_RESIZE || cursor == Cursor.SE_RESIZE;

                double newX = this.getX();
                double newY = this.getY();
                double newWidth = this.getWidth();
                double newHeight = this.getHeight();
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
        });
        node.setOnMouseReleased(event -> {
            startLocation.setValue(null);
            if (savedCursor.get() != null && isResizable) {

                this.setX(node.getLayoutX());
                this.setY(node.getLayoutY());
                this.setWidth(((Region) node).getPrefWidth());
                this.setHeight(((Region) node).getPrefHeight());
                savedCursor.setValue(null);
            }
        });

    }

    /**
     * Set highlighted state of the node if node can be resized or moved
     *
     * @param b - true if node should be highlighted
     */
    private void setHighlighted(boolean b) {
        if (draggingDisabled && resizableDisabled) {
            return;
        }
        if (b) {
            node.getStyleClass().add("highlighted");
        } else {
            node.getStyleClass().remove("highlighted");
        }
    }


    public void setX(double x) {
        this.x.set(x);
    }

    public void setY(double y) {
        this.y.set(y);
    }

    public double getX() {
        return x.get();
    }

    public double getY() {
        return y.get();
    }

    public void setHeight(double height) {
        this.height.set(height);
    }

    public void setWidth(double width) {
        this.width.set(width);
    }

    public double getHeight() {
        return height.get();
    }

    public double getWidth() {
        return width.get();
    }

    protected void setupPositionAndSize(Node node) {
        if (x.get() >= 0) {
            node.setLayoutX(x.get());
        }
        if (y.get() >= 0) {
            node.setLayoutY(y.get());
        }
        if (node instanceof Region region) {
            if (width.get() >= 0) {
                region.setPrefWidth(width.get());
            }
            if (height.get() >= 0) {
                region.setPrefHeight(height.get());
            }
        }
    }

    public List<String> dump() {
        return List.of(" - " + getClass().getSimpleName());
    }

    protected void disableResizing() {
        this.resizableDisabled = true;
    }

    protected void disableDragging() {
        this.draggingDisabled = true;
    }

    public void addBoundaryListener(ChangeListener<Number> o) {
        addSizeListener(o);
        addPositionListener(o);
    }

    public void addPositionListener(ChangeListener<Number> o) {
        x.addListener(o);
        y.addListener(o);
    }

    public void addSizeListener(ChangeListener<Number> o) {
        addWidthListener(o);
        addHeightListener(o);
    }

    public void addWidthListener(ChangeListener<Number> o) {
        width.addListener(o);
    }

    public void addHeightListener(ChangeListener<Number> o) {
        height.addListener(o);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    public void setType(StickerElementTypes type) {
        this.type = type;
    }

    public StickerElementTypes getType() {
        return type;
    }

    public abstract void deserialize(JsonObject properties);

    public abstract JsonObject serialize();

    public record Provider(StickerElementTypes type) {


        public StickerElement<? extends Node> get() {
            StickerElement<? extends Node> stickerElement = type.getSupplier().get();
            stickerElement.setType(type);
            return stickerElement;
        }

        @Override
        public String toString() {
            return type.getName();
        }

    }
}
