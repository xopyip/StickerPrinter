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

        if (elementActionType.isResizeTop()) resizeTop(event);
        if (elementActionType.isResizeBottom()) resizeBottom(event);
        if (elementActionType.isResizeLeft()) resizeLeft(event);
        if (elementActionType.isResizeRight()) resizeRight(event);
    }

    private void resizeRight(StickerElement.ElementMouseDraggedEvent event) {
        Region region = (Region) event.elementNode();
        resize(event, ResizeMode.HORIZONTAL, 0,
                event.mouseEvent().getSceneX() - region.getLayoutX() - event.getOrigin().getX() - region.getPrefWidth());


    }

    private void resizeLeft(StickerElement.ElementMouseDraggedEvent event) {
        Region region = (Region) event.elementNode();
        resize(event, ResizeMode.HORIZONTAL,
                event.mouseEvent().getSceneX() - event.getOrigin().getX() - region.getLayoutX(),
                event.getOrigin().getX() + region.getLayoutX() - event.mouseEvent().getSceneX());

    }

    private void resizeBottom(StickerElement.ElementMouseDraggedEvent event) {
        Region region = (Region) event.elementNode();
        resize(event, ResizeMode.VERTICAL, 0,
                event.mouseEvent().getSceneY() - region.getLayoutY() - event.getOrigin().getY() - region.getPrefHeight());

    }

    private void resizeTop(StickerElement.ElementMouseDraggedEvent event) {
        Region region = (Region) event.elementNode();
        resize(event, ResizeMode.VERTICAL,
                event.mouseEvent().getSceneY() - event.getOrigin().getY() - region.getLayoutY(),
                event.getOrigin().getY() + region.getLayoutY() - event.mouseEvent().getSceneY());

    }

    private void resize(StickerElement.ElementMouseDraggedEvent event,
                               ResizeMode orientation,
                               double pos, double size) {
        double dx = 0, dy = 0, dw = 0, dh = 0;
        //todo: missing preserve ratio
        switch (orientation) {
            case HORIZONTAL -> {
                dx = pos;
                dw = size;
            }
            case VERTICAL -> {
                dy = pos;
                dh = size;
            }
        }

        Bounds parentBounds = event.parentPane().getBoundsInLocal();
        double parentMaxY = parentBounds.getMinY() + event.parentPane().getPrefHeight();
        double parentMaxX = parentBounds.getMinX() + event.parentPane().getPrefWidth();
        Region region = (Region) event.elementNode();
        if (region.getLayoutX() + dx < 0 || region.getLayoutY() + dy < 0) {
            return;
        }
        if (region.getLayoutX() + dx + region.getPrefWidth() + dw > parentMaxX || region.getLayoutY() + dy + region.getPrefHeight() + dh > parentMaxY) {
            return;
        }
        region.setLayoutX(region.getLayoutX() + dx);
        region.setLayoutY(region.getLayoutY() + dy);
        region.setPrefWidth(region.getPrefWidth() + dw);
        region.setPrefHeight(region.getPrefHeight() + dh);
    }

    enum ResizeMode{
        VERTICAL, HORIZONTAL
    }
}
