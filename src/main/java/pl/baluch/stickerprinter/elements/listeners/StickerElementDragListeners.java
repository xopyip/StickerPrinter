package pl.baluch.stickerprinter.elements.listeners;

import com.google.common.eventbus.Subscribe;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pl.baluch.stickerprinter.data.ElementActionType;
import pl.baluch.stickerprinter.elements.ContainerStickerElement;
import pl.baluch.stickerprinter.elements.StickerElement;

public class StickerElementDragListeners {

    @Subscribe
    private void onDragging(StickerElement.ElementMouseDraggedEvent event) {
        if (!event.element().isDraggable(event.parentPane(), event.drawContext())) {
            return;
        }
        if (event.parentPane() instanceof AnchorPane) {
            dragOnAnchorPane(event);
        } else if (event.parentPane() instanceof HBox) {
            dragOnHBox(event);
        } else if (event.parentPane() instanceof VBox) {
            dragOnVBox(event);
        } else {
            throw new RuntimeException("Moving on " + event.parentPane().getClass().getName() + " is not supported");
        }
    }

    private void dragOnVBox(StickerElement.ElementMouseDraggedEvent event) {
        if (event.element().getElementAction() != ElementActionType.DRAG) {
            return;
        }
        VBox vBox = (VBox) event.parentPane();
        double y = event.mouseEvent().getY() + event.elementNode().getBoundsInParent().getMinY();

        int i = vBox.getChildren().indexOf(event.elementNode());

        ObservableList<Node> children = vBox.getChildren();

        if (i > 0 && children.get(i - 1).getBoundsInParent().getCenterY() > y) { //move up
            event.element().getParent().map(ContainerStickerElement::getChildren).ifPresent(list -> {
                list.remove(i);
                list.add(i - 1, event.element());
                children.remove(i);
                children.add(i - 1, event.elementNode());
            });
        } else if (i < children.size() - 1 && children.get(i + 1).getBoundsInParent().getCenterY() < y) { //move down
            event.element().getParent().map(ContainerStickerElement::getChildren).ifPresent(list -> {
                list.remove(i);
                list.add(i + 1, event.element());
                children.remove(i);
                children.add(i + 1, event.elementNode());
            });
        }
    }

    private void dragOnHBox(StickerElement.ElementMouseDraggedEvent event) {
        throw new RuntimeException("Moving on " + event.parentPane().getClass().getName() + " is not supported");
    }

    private void dragOnAnchorPane(StickerElement.ElementMouseDraggedEvent event) {
        if (event.element().getElementAction() != ElementActionType.DRAG) {
            return;
        }
        double x = event.mouseEvent().getX();
        double y = event.mouseEvent().getY();
        Point2D point2D = event.element().getDragStartLocation();
        if (event.element().getDragStartLocation() == null) {
            event.element().setDragStartLocation(point2D = new Point2D(event.mouseEvent().getX(), event.mouseEvent().getY()));
        }
        x -= point2D.getX();
        y -= point2D.getY();

        Bounds parentBounds = event.parentPane().getBoundsInLocal();
        double parentMaxX = parentBounds.getMinX() + event.parentPane().getPrefWidth();
        double parentMaxY = parentBounds.getMinY() + event.parentPane().getPrefHeight();

        double newX = event.element().getX() + x;
        newX = Math.max(newX, parentBounds.getMinX()); // left
        newX = Math.min(newX, parentMaxX - event.element().getWidth()); // right
        double newY = event.element().getY() + y;
        newY = Math.max(newY, parentBounds.getMinY()); // top
        newY = Math.min(newY, parentMaxY - event.element().getHeight()); // bottom

        event.element().setX(newX);
        event.element().setY(newY);

    }

}
