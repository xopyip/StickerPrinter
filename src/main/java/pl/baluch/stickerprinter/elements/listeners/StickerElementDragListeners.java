package pl.baluch.stickerprinter.elements.listeners;

import com.google.common.eventbus.Subscribe;
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

            if (event.parentPane().contains(event.elementNode().getLayoutX() + x, event.elementNode().getLayoutY() + y)) {
                event.element().setX(x + event.elementNode().getLayoutX());
                event.element().setY(y + event.elementNode().getLayoutY());
            }
        }
    }

}
