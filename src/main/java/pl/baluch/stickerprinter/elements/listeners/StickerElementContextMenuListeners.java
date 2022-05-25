package pl.baluch.stickerprinter.elements.listeners;

import com.google.common.eventbus.Subscribe;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import pl.baluch.stickerprinter.AppMain;
import pl.baluch.stickerprinter.Storage;
import pl.baluch.stickerprinter.elements.StickerElement;
import pl.baluch.stickerprinter.events.element.BringDownStickerElementEvent;
import pl.baluch.stickerprinter.events.element.BringUpStickerElementEvent;
import pl.baluch.stickerprinter.events.element.DeleteStickerElementEvent;

public class StickerElementContextMenuListeners {
    private final ContextMenu contextMenu = new ContextMenu();

    @Subscribe
    private void contextMenuEvent(StickerElement.ElementMouseReleasedEvent event) {
        if (event.element().getDragStartLocation() == null) {
            contextMenu.hide();
            if (event.mouseEvent().getButton() == MouseButton.SECONDARY) {
                contextMenu.getItems().clear();

                contextMenu.getItems().add(createDeleteItem(event.element()));
                contextMenu.getItems().add(createBringUpItem(event.element()));
                contextMenu.getItems().add(createBringDownItem(event.element()));

                contextMenu.show(event.elementNode(), event.mouseEvent().getScreenX(), event.mouseEvent().getScreenY());
            }
        }
        event.element().setDragStartLocation(null);
    }

    private MenuItem createBringDownItem(StickerElement<?> element) {
        MenuItem bringDown = new MenuItem(Storage.getResourceBundle().getString("context.menu.bring.down"));
        bringDown.setOnAction(bringDownEvent -> AppMain.EVENT_BUS.post(new BringDownStickerElementEvent(element)));
        return bringDown;
    }

    private MenuItem createBringUpItem(StickerElement<?> element) {
        MenuItem bringUp = new MenuItem(Storage.getResourceBundle().getString("context.menu.bring.up"));
        bringUp.setOnAction(bringUpEvent -> AppMain.EVENT_BUS.post(new BringUpStickerElementEvent(element)));
        return bringUp;
    }

    private MenuItem createDeleteItem(StickerElement<?> element) {
        MenuItem delete = new MenuItem(Storage.getResourceBundle().getString("context.menu.delete"));
        delete.setOnAction(deleteEvent -> AppMain.EVENT_BUS.post(new DeleteStickerElementEvent(element)));
        return delete;
    }
}
