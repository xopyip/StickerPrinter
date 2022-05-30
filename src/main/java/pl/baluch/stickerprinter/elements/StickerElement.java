package pl.baluch.stickerprinter.elements;

import com.google.common.eventbus.EventBus;
import com.google.gson.JsonObject;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import pl.baluch.stickerprinter.AppMain;
import pl.baluch.stickerprinter.data.DrawContext;
import pl.baluch.stickerprinter.data.ElementActionType;
import pl.baluch.stickerprinter.data.PreviewPane;
import pl.baluch.stickerprinter.data.StickerElementProperty;
import pl.baluch.stickerprinter.elements.listeners.StickerElementContextMenuListeners;
import pl.baluch.stickerprinter.elements.listeners.StickerElementDragListeners;
import pl.baluch.stickerprinter.elements.listeners.StickerElementResizeListeners;
import pl.baluch.stickerprinter.events.element.SelectStickerElementEvent;
import pl.baluch.stickerprinter.plugins.Item;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class StickerElement<T extends Node> {
    protected SimpleDoubleProperty x = new SimpleDoubleProperty(0);
    protected SimpleDoubleProperty y = new SimpleDoubleProperty(0);
    protected SimpleDoubleProperty width = new SimpleDoubleProperty(-1);
    protected SimpleDoubleProperty height = new SimpleDoubleProperty(-1);
    private boolean resizableDisabled = false;
    private boolean draggingDisabled = false;
    protected SimpleObjectProperty<StickerElement<?>> mouseOverItemProperty = new SimpleObjectProperty<>(null);
    protected SimpleObjectProperty<ElementActionType> elementActionProperty = new SimpleObjectProperty<>();
    protected SimpleObjectProperty<Point2D> dragStartLocation = new SimpleObjectProperty<>();

    protected WeakReference<T> nodeReference = new WeakReference<>(null);
    protected WeakReference<DrawContext> contextReference = new WeakReference<>(null);
    protected static final EventBus elementEventBus = new EventBus();

    static {
        elementEventBus.register(new StickerElementDragListeners());
        elementEventBus.register(new StickerElementResizeListeners());
        elementEventBus.register(new StickerElementContextMenuListeners());
    }

    //Stack to trace hover state
    private boolean selected;
    private final Collection<StickerElementProperty> properties = new ArrayList<>();
    private float preservedRatio = -1;
    private WeakReference<ContainerStickerElement<?>> parentReference = new WeakReference<>(null);

    public StickerElement(int x, int y, int w, int h) {
        this.x.set(x);
        this.y.set(y);
        this.width.set(w);
        this.height.set(h);
    }
    public StickerElement(){
    }

    public abstract void draw(Pane pane, DrawContext drawContext, ContainerStickerElement<?> parent);

    public abstract StickerElementTypes getType();

    protected void setupNode(Pane parentPane, T elementNode, DrawContext drawContext, ContainerStickerElement<?> parent) {
        nodeReference = new WeakReference<>(elementNode);
        contextReference = new WeakReference<>(drawContext);
        parentReference = new WeakReference<>(parent);
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
            if (event.getButton() == MouseButton.PRIMARY) {
                AppMain.EVENT_BUS.post(new SelectStickerElementEvent(mouseOverItemProperty.get()));
            }
            event.consume();
        });
        node.setOnMouseMoved(event -> {
            if (!isMouseOver()) {
                elementActionProperty.set(ElementActionType.NONE);
                return;
            }
            setMouseAction(node, event);
            setCursorBasedOnAction(parentPane, node, drawContext);
        });
    }

    private void setCursorBasedOnAction(Pane parentPane, Node node, DrawContext drawContext) {
        if (elementActionProperty.get().canSetCursor(isResizable(node, drawContext), isDraggable(parentPane, drawContext))) {
            node.setCursor(elementActionProperty.get().getCursor());
        }
    }

    private void setMouseAction(Node node, MouseEvent event) {
        if (node.contains(event.getX(), event.getY())) {
            elementActionProperty.set(ElementActionType.DRAG);
        }
        if (node instanceof Region) {
            setResizeActionBasedOnMousePosition(event, (Region) node);
        }
    }

    private void setResizeActionBasedOnMousePosition(MouseEvent event, Region node) {
        double cursorX = event.getX();
        double cursorY = event.getY();
        boolean left = cursorX <= 1;
        boolean right = cursorX >= node.getPrefWidth() - 1;
        boolean top = cursorY <= 1;
        boolean bottom = cursorY >= node.getPrefHeight() - 1;

        if (left && top) elementActionProperty.set(ElementActionType.RESIZE_TOP_LEFT);
        else if (left && bottom) elementActionProperty.set(ElementActionType.RESIZE_BOTTOM_LEFT);
        else if (right && top) elementActionProperty.set(ElementActionType.RESIZE_TOP_RIGHT);
        else if (right && bottom) elementActionProperty.set(ElementActionType.RESIZE_BOTTOM_RIGHT);
        else if (left) elementActionProperty.set(ElementActionType.RESIZE_LEFT);
        else if (right) elementActionProperty.set(ElementActionType.RESIZE_RIGHT);
        else if (top) elementActionProperty.set(ElementActionType.RESIZE_TOP);
        else if (bottom) elementActionProperty.set(ElementActionType.RESIZE_BOTTOM);
    }

    public boolean isDraggable(Pane parentPane, DrawContext drawContext) {
        return (parentPane instanceof AnchorPane || parentPane instanceof HBox || parentPane instanceof VBox) && !draggingDisabled && drawContext.isEditor();
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

    protected void bindBounds(Node node) {
        node.layoutXProperty().bindBidirectional(x);
        node.layoutYProperty().bindBidirectional(y);
        if (node instanceof Region region) {
            region.prefWidthProperty().bindBidirectional(width);
            region.prefHeightProperty().bindBidirectional(height);
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

    public Optional<T> getNode() {
        return Optional.ofNullable(nodeReference.get());
    }

    public Optional<DrawContext> getContext() {
        return Optional.ofNullable(contextReference.get());
    }

    public float getPreservedRatio() {
        return preservedRatio;
    }

    protected String formatPropertyValue(String text, Item item) {
        if (text.startsWith(":")) {
            String key = text.substring(1);
            if (item.isCustomProperty(key)) {
                return "???";
            } else {
                return item.getPropertyValue(key);
            }
        } else {
            return text;
        }
    }

    protected void preserveRatio(int w, int h) {
        this.preservedRatio = w * 1.f / h;
    }

    public Optional<ContainerStickerElement<?>> getParent() {
        return Optional.ofNullable(parentReference.get());
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
        public Point2D getOrigin() {
            Node node = elementNode;
            while (!(node instanceof PreviewPane)) {
                node = node.getParent();
            }
            Bounds layoutBounds = node.localToScene(node.getBoundsInLocal());
            return new Point2D(layoutBounds.getMinX(), layoutBounds.getMinY());
        }
    }
}
