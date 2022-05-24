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
            Region region = (Region) event.elementNode();
            Point2D origin = event.getOrigin();

            if (elementActionType.isResizeTop()) {
                double oldMaxY = region.getLayoutY() + region.getPrefHeight();
                region.setLayoutY(event.mouseEvent().getSceneY() - origin.getY());
                region.setPrefHeight(oldMaxY - region.getLayoutY());
            }
            if (elementActionType.isResizeLeft()) {
                double oldMaxX = region.getLayoutX() + region.getPrefWidth();
                region.setLayoutX(event.mouseEvent().getSceneX() - origin.getX());
                region.setPrefWidth(oldMaxX - region.getLayoutX());
            }
            if (elementActionType.isResizeRight()) {
                region.setPrefWidth(event.mouseEvent().getSceneX() - region.getLayoutX() - origin.getX());
            }
            if (elementActionType.isResizeBottom()) {
                region.setPrefHeight(event.mouseEvent().getSceneY() - region.getLayoutY() - origin.getY());
            }
        }
    }
}
