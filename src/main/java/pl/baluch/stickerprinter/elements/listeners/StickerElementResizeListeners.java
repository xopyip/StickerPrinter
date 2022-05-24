package pl.baluch.stickerprinter.elements.listeners;

import com.google.common.eventbus.Subscribe;
import javafx.geometry.Point2D;
import javafx.scene.layout.Region;
import pl.baluch.stickerprinter.data.ElementActionType;
import pl.baluch.stickerprinter.elements.StickerElement;

public class StickerElementResizeListeners {

    @Subscribe
    private void onDraggedForResize(StickerElement.ElementMouseDraggedEvent event) {
        if (!event.element().isResizable(event.elementNode(), event.drawContext())) {
            return;
        }

        if (event.element().getElementAction().isResize()) {
            ElementActionType elementActionType = event.element().getElementAction();
            Point2D point2D = event.element().getDragStartLocation();
            if (event.element().getDragStartLocation() == null) {
                event.element().setDragStartLocation(point2D = new Point2D(event.mouseEvent().getScreenX(), event.mouseEvent().getScreenY()));
            }
            Region region = (Region) event.elementNode();

            double newX = event.element().getX();
            double newY = event.element().getY();
            double newWidth = event.element().getWidth();
            double newHeight = event.element().getHeight();
            double dx = event.mouseEvent().getScreenX() - point2D.getX();
            double dy = event.mouseEvent().getScreenY() - point2D.getY();
            if (elementActionType.isResizeTop()) {
                newY += dy;
                newHeight -= dy;
            }
            if (elementActionType.isResizeLeft()) {
                newX += dx;
                newWidth -= dx;
            }
            if (elementActionType.isResizeRight()) {
                newWidth += dx;
            }
            if (elementActionType.isResizeBottom()) {
                newHeight += dy;
            }
            region.setLayoutX(newX);
            region.setLayoutY(newY);
            region.setPrefWidth(newWidth);
            region.setPrefHeight(newHeight);
        }
    }

    @Subscribe
    private void onReleasedForResize(StickerElement.ElementMouseReleasedEvent event) {
        event.element().setX(event.elementNode().getLayoutX());
        event.element().setY(event.elementNode().getLayoutY());
        event.element().setWidth(((Region) event.elementNode()).getPrefWidth());
        event.element().setHeight(((Region) event.elementNode()).getPrefHeight());
        event.element().setDragStartLocation(null);
    }
}
