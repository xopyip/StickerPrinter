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
        float preservedRatio = event.element().getPreservedRatio();

        if (event.element().getElementAction().isResize()) {
            ElementActionType elementActionType = event.element().getElementAction();
            Region region = (Region) event.elementNode();
            Point2D origin = event.getOrigin();

            if (elementActionType.isResizeTop()) {
                double oldMaxY = region.getLayoutY() + region.getPrefHeight();
                region.setLayoutY(event.mouseEvent().getSceneY() - origin.getY());
                region.setPrefHeight(oldMaxY - region.getLayoutY());
                if (preservedRatio > 0) {
                    region.setPrefWidth(region.getPrefHeight() * preservedRatio);
                }
            }
            if (elementActionType.isResizeLeft()) {
                double oldMaxX = region.getLayoutX() + region.getPrefWidth();
                region.setLayoutX(event.mouseEvent().getSceneX() - origin.getX());
                region.setPrefWidth(oldMaxX - region.getLayoutX());
                if (preservedRatio > 0) {
                    region.setPrefHeight(region.getPrefWidth() / preservedRatio);
                }
            }
            if (elementActionType.isResizeRight()) {
                region.setPrefWidth(event.mouseEvent().getSceneX() - region.getLayoutX() - origin.getX());
                if (preservedRatio > 0) {
                    region.setPrefHeight(region.getPrefWidth() / preservedRatio);
                }
            }
            if (elementActionType.isResizeBottom()) {
                region.setPrefHeight(event.mouseEvent().getSceneY() - region.getLayoutY() - origin.getY());
                if (preservedRatio > 0) {
                    region.setPrefWidth(region.getPrefHeight() * preservedRatio);
                }
            }
        }
    }
}
