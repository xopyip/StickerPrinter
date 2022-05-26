package pl.baluch.stickerprinter.elements.listeners;

import com.google.common.eventbus.Subscribe;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import pl.baluch.stickerprinter.data.ElementActionType;
import pl.baluch.stickerprinter.elements.StickerElement;

public class StickerElementDragListeners {

    @Subscribe
    private void onDragging(StickerElement.ElementMouseDraggedEvent event) {
        if (!event.element().isDraggable(event.parentPane(), event.drawContext())) {
            return;
        }
        if (event.element().getElementAction() == ElementActionType.DRAG) {
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

}
