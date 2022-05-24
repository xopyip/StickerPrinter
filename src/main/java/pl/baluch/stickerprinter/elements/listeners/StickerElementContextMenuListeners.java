package pl.baluch.stickerprinter.elements.listeners;

import com.google.common.eventbus.Subscribe;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import pl.baluch.stickerprinter.AppMain;
import pl.baluch.stickerprinter.elements.StickerElement;
import pl.baluch.stickerprinter.events.DeleteStickerElementEvent;

public class StickerElementContextMenuListeners {
    private final ContextMenu contextMenu = new ContextMenu();

    @Subscribe
    private void contextMenuEvent(StickerElement.ElementMouseReleasedEvent event) {
        if (event.element().getDragStartLocation() == null) {
            contextMenu.hide();
            if (event.mouseEvent().getButton() == MouseButton.SECONDARY) {
                MenuItem delete = new MenuItem("Delete");
                delete.setOnAction(deleteEvent -> {
                    AppMain.EVENT_BUS.post(new DeleteStickerElementEvent(event.element()));
                    event.mouseEvent().consume();
                });
                contextMenu.getItems().clear();
                contextMenu.getItems().add(delete);
                contextMenu.show(event.elementNode(), event.mouseEvent().getScreenX(), event.mouseEvent().getScreenY());
            }
        }
    }
}
