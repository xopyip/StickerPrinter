package pl.baluch.stickerprinter.elements;

import com.google.common.eventbus.EventBus;
import com.google.gson.JsonObject;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import pl.baluch.stickerprinter.AppMain;
import pl.baluch.stickerprinter.data.DrawContext;
import pl.baluch.stickerprinter.data.ElementActionType;
import pl.baluch.stickerprinter.data.StickerElementProperty;
import pl.baluch.stickerprinter.elements.listeners.StickerElementContextMenuListeners;
import pl.baluch.stickerprinter.elements.listeners.StickerElementDragListeners;
import pl.baluch.stickerprinter.elements.listeners.StickerElementResizeListeners;
import pl.baluch.stickerprinter.events.SelectStickerElementEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public abstract class StickerElement<T extends Node> {
    protected SimpleDoubleProperty x = new SimpleDoubleProperty(0);
    protected SimpleDoubleProperty y = new SimpleDoubleProperty(0);
    protected SimpleDoubleProperty width = new SimpleDoubleProperty(-1);
    protected SimpleDoubleProperty height = new SimpleDoubleProperty(-1);
    private boolean resizableDisabled = false;
    private boolean draggingDisabled = false;
    protected transient Supplier<T> nodeSupplier;
    protected transient SimpleObjectProperty<StickerElement<?>> mouseOverItemProperty = new SimpleObjectProperty<>(null);
    protected transient SimpleObjectProperty<ElementActionType> elementActionProperty = new SimpleObjectProperty<>();
    protected transient SimpleObjectProperty<Point2D> dragStartLocation = new SimpleObjectProperty<>();
    protected static final EventBus elementEventBus = new EventBus();

    static {
        elementEventBus.register(new StickerElementDragListeners());
        elementEventBus.register(new StickerElementResizeListeners());
        elementEventBus.register(new StickerElementContextMenuListeners());
    }

    //Stack to trace hover state
    private StickerElementTypes type;
    private boolean selected;
    private final Collection<StickerElementProperty> properties = new ArrayList<>();

    public StickerElement(Supplier<T> node, int x, int y, int w, int h) {
        this.nodeSupplier = node;
        this.x.set(x);
        this.y.set(y);
        this.width.set(w);
        this.height.set(h);
    }

    public StickerElement(Supplier<T> node) {
        this.nodeSupplier = node;
    }

    public abstract void draw(Pane pane, DrawContext drawContext);

    /**
     * Sets required listeners and properties to make element resizable and draggable
     *
     * @param parentPane  - parent pane
     * @param elementNode - node to be resized and dragged
     * @param drawContext - context of drawing
     */
    protected void setupNode(Pane parentPane, Node elementNode, DrawContext drawContext) {
        this.setupEventListeners(parentPane, elementNode, drawContext);
        if (selected) {
            elementNode.getStyleClass().add("element-selected");
        }
        if (isResizable(elementNode, drawContext)) {
            ((Region) elementNode).setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.DOTTED, null, BorderStroke.THIN)));
        }
        this.setupHighlighting(elementNode);
    }

    private void setupEventListeners(Pane parentPane, Node node, DrawContext drawContext) {
        node.setOnMouseEntered(event -> mouseOverItemProperty.set(this));
        node.setOnMouseExited(event -> mouseOverItemProperty.set(null));
        node.setOnMouseDragged(event -> {
            elementEventBus.post(new ElementMouseDraggedEvent(this, parentPane, node, drawContext, event));
            event.consume();
        });
        node.setOnMouseReleased(event -> {
            elementEventBus.post(new ElementMouseReleasedEvent(this, parentPane, node, drawContext, event));
            event.consume();
        });
        node.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.PRIMARY) {
                AppMain.EVENT_BUS.post(new SelectStickerElementEvent(mouseOverItemProperty.get()));
            }
            event.consume();
        });
        node.setOnMouseMoved(event -> {
            if (!isMouseOver()) {
                elementActionProperty.set(ElementActionType.NONE);
                return;
            }

            if (node.contains(event.getX(), event.getY())) {
                elementActionProperty.set(ElementActionType.DRAG);
            }
            if (node instanceof Region) {
                double cursorX = event.getX();
                double cursorY = event.getY();
                boolean left = cursorX <= 1;
                boolean right = cursorX >= ((Region) node).getPrefWidth() - 1;
                boolean top = cursorY <= 1;
                boolean bottom = cursorY >= ((Region) node).getPrefHeight() - 1;

                if (left && top) elementActionProperty.set(ElementActionType.RESIZE_TOP_LEFT);
                else if (left && bottom) elementActionProperty.set(ElementActionType.RESIZE_BOTTOM_LEFT);
                else if (right && top) elementActionProperty.set(ElementActionType.RESIZE_TOP_RIGHT);
                else if (right && bottom) elementActionProperty.set(ElementActionType.RESIZE_BOTTOM_RIGHT);
                else if (left) elementActionProperty.set(ElementActionType.RESIZE_LEFT);
                else if (right) elementActionProperty.set(ElementActionType.RESIZE_RIGHT);
                else if (top) elementActionProperty.set(ElementActionType.RESIZE_TOP);
                else if (bottom) elementActionProperty.set(ElementActionType.RESIZE_BOTTOM);
            }
            if (elementActionProperty.get().canSetCursor(isResizable(node, drawContext), isDraggable(parentPane, drawContext))) {
                node.setCursor(elementActionProperty.get().getCursor());
            }
        });
    }

    public boolean isDraggable(Pane parentPane, DrawContext drawContext) {
        return (parentPane instanceof AnchorPane) && !draggingDisabled && drawContext.isEditor();
    }

    public boolean isResizable(Node elementNode, DrawContext drawContext) {
        return (elementNode instanceof Region) && !resizableDisabled && drawContext.isEditor();
    }

    private void setupHighlighting(Node elementNode) {
        mouseOverItemProperty.addListener((observable, oldValue, newValue) -> setHighlighted(elementNode, newValue == this));
    }


    private boolean isMouseOver() {
        return mouseOverItemProperty.get() == this;
    }

    /**
     * Set highlighted state of the node if node can be resized or moved
     *
     * @param node - node to be highlighted
     * @param b    - true if node should be highlighted
     */
    private void setHighlighted(Node node, boolean b) {
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

    public Point2D getDragStartLocation() {
        return dragStartLocation.get();
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

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Collection<StickerElementProperty> getProperties() {
        return properties;
    }

    public void addProperty(StickerElementProperty property) {
        properties.add(property);
    }

    public ElementActionType getElementAction() {
        return elementActionProperty.get();
    }

    public void setDragStartLocation(Point2D point2D) {
        dragStartLocation.set(point2D);
    }

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

    public record ElementMouseReleasedEvent(StickerElement<?> element,
                                            Pane parentPane,
                                            Node elementNode,
                                            DrawContext drawContext,
                                            MouseEvent mouseEvent) {
    }

    public record ElementMouseDraggedEvent(StickerElement<?> element,
                                           Pane parentPane,
                                           Node elementNode,
                                           DrawContext drawContext,
                                           MouseEvent mouseEvent) {
    }
}
