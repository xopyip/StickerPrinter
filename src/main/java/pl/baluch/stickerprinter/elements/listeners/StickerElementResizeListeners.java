package pl.baluch.stickerprinter.elements.listeners;

import com.google.common.eventbus.Subscribe;
import javafx.geometry.Bounds;
import javafx.scene.layout.Region;
import pl.baluch.stickerprinter.data.ElementActionType;
import pl.baluch.stickerprinter.elements.StickerElement;

public class StickerElementResizeListeners {

    @Subscribe
    private void onDraggedForResize(StickerElement.ElementMouseDraggedEvent event) {
        StickerElement<?> element = event.element();
        if (!element.isResizable(event.elementNode(), event.drawContext())) {
            return;
        }
        ElementActionType elementActionType = element.getElementAction();

        if (elementActionType.isResizeTop()) performResizeTop(event);
        if (elementActionType.isResizeBottom()) performResizeBottom(event);
        if (elementActionType.isResizeLeft()) performResizeLeft(event);
        if (elementActionType.isResizeRight()) performResizeRight(event);
    }

    private void performResizeRight(StickerElement.ElementMouseDraggedEvent event) {
        Bounds parentBounds = event.parentPane().getBoundsInLocal();
        double parentMaxX = parentBounds.getMinX() + event.parentPane().getPrefWidth();
        if (event.mouseEvent().getSceneX() - event.getOrigin().getX() >= parentMaxX) {
            return;
        }

        Region region = (Region) event.elementNode();
        float preservedRatio = event.element().getPreservedRatio();
        region.setPrefWidth(event.mouseEvent().getSceneX() - region.getLayoutX() - event.getOrigin().getX());

        if (preservedRatio > 0) {
            region.setPrefHeight(region.getPrefWidth() / preservedRatio);
        }

    }

    private void performResizeLeft(StickerElement.ElementMouseDraggedEvent event) {
        if (event.mouseEvent().getSceneX() - event.getOrigin().getX() <= 0) {
            return;
        }

        Region region = (Region) event.elementNode();
        float preservedRatio = event.element().getPreservedRatio();
        double oldMaxX = region.getLayoutX() + region.getPrefWidth();
        region.setLayoutX(event.mouseEvent().getSceneX() - event.getOrigin().getX());
        region.setPrefWidth(oldMaxX - region.getLayoutX());
        if (preservedRatio > 0) {
            region.setPrefHeight(region.getPrefWidth() / preservedRatio);
        }

    }

    private void performResizeBottom(StickerElement.ElementMouseDraggedEvent event) {
        Bounds parentBounds = event.parentPane().getBoundsInLocal();
        double parentMaxY = parentBounds.getMinY() + event.parentPane().getPrefHeight();
        if (event.mouseEvent().getSceneY() - event.getOrigin().getY() >= parentMaxY) {
            return;
        }

        Region region = (Region) event.elementNode();
        float preservedRatio = event.element().getPreservedRatio();
        region.setPrefHeight(event.mouseEvent().getSceneY() - region.getLayoutY() - event.getOrigin().getY());
        if (preservedRatio > 0) {
            region.setPrefWidth(region.getPrefHeight() * preservedRatio);
        }

    }

    private void performResizeTop(StickerElement.ElementMouseDraggedEvent event) {
        if (event.mouseEvent().getSceneY() - event.getOrigin().getY() <= 0) {
            return;
        }

        float preservedRatio = event.element().getPreservedRatio();
        Region region = (Region) event.elementNode();
        double oldMaxY = region.getLayoutY() + region.getPrefHeight();
        region.setLayoutY(event.mouseEvent().getSceneY() - event.getOrigin().getY());
        region.setPrefHeight(oldMaxY - region.getLayoutY());
        if (preservedRatio > 0) {
            region.setPrefWidth(region.getPrefHeight() * preservedRatio);
        }

    }
}
