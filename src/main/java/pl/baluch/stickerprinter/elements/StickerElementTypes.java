package pl.baluch.stickerprinter.elements;

import javafx.scene.Node;
import pl.baluch.stickerprinter.Storage;
import pl.baluch.stickerprinter.elements.children.HSpacer;
import pl.baluch.stickerprinter.elements.children.Text;
import pl.baluch.stickerprinter.elements.children.VSpacer;
import pl.baluch.stickerprinter.elements.containers.HBox;
import pl.baluch.stickerprinter.elements.containers.StickerAnchorPane;

import java.util.function.Supplier;

public enum StickerElementTypes {
    ANCHOR_PANE(() -> Storage.getResourceBundle().getString("sticker.elements.anchorPane"), StickerAnchorPane::new),
    TEXT(() -> Storage.getResourceBundle().getString("sticker.elements.text"), Text::new),
    V_SPACER(() -> Storage.getResourceBundle().getString("sticker.elements.vSpacer"), VSpacer::new),
    H_SPACER(() -> Storage.getResourceBundle().getString("sticker.elements.hSpacer"), HSpacer::new),
    H_BOX(() -> Storage.getResourceBundle().getString("sticker.elements.hBox"), HBox::new),
    LABEL(() -> Storage.getResourceBundle().getString("sticker.elements.label"), pl.baluch.stickerprinter.elements.children.Label::new),
    ;

    private final Supplier<String> name;
    private final Supplier<StickerElement<? extends Node>> supplier;

    StickerElementTypes(Supplier<String> name, Supplier<StickerElement<? extends Node>> supplier) {
        this.name = name;
        this.supplier = supplier;
    }

    public String getName() {
        return name.get();
    }

    public Supplier<StickerElement<? extends Node>> getSupplier() {
        return supplier;
    }
}
