package pl.baluch.stickerprinter.elements;

import javafx.scene.Node;
import pl.baluch.stickerprinter.Storage;
import pl.baluch.stickerprinter.elements.children.HSpacer;
import pl.baluch.stickerprinter.elements.children.Text;
import pl.baluch.stickerprinter.elements.children.VSpacer;
import pl.baluch.stickerprinter.elements.containers.HBox;
import pl.baluch.stickerprinter.elements.containers.StickerAnchorPane;
import pl.baluch.stickerprinter.elements.containers.TextFlow;

import java.util.function.Predicate;
import java.util.function.Supplier;

public enum StickerElementTypes {
    ANCHOR_PANE(() -> Storage.getResourceBundle().getString("sticker.elements.anchorPane"), StickerAnchorPane::new, (parent) -> true),
    TEXT_FLOW(() -> Storage.getResourceBundle().getString("sticker.elements.textFlow"), TextFlow::new, (parent) -> true),
    TEXT(() -> Storage.getResourceBundle().getString("sticker.elements.text"), Text::new, (parent) -> parent instanceof TextFlow),
    V_SPACER(() -> Storage.getResourceBundle().getString("sticker.elements.vSpacer"), VSpacer::new, (parent) -> parent instanceof HBox),
    H_SPACER(() -> Storage.getResourceBundle().getString("sticker.elements.hSpacer"), HSpacer::new, (parent) -> parent instanceof HBox),
    H_BOX(() -> Storage.getResourceBundle().getString("sticker.elements.hBox"), HBox::new, (parent) -> true),
    LABEL(() -> Storage.getResourceBundle().getString("sticker.elements.label"), pl.baluch.stickerprinter.elements.children.Label::new, (parent) -> true),
    ;

    private final Supplier<String> name;
    private final Supplier<StickerElement<? extends Node>> supplier;
    private final Predicate<ContainerStickerElement<?>> parentValidator;

    StickerElementTypes(Supplier<String> name, Supplier<StickerElement<? extends Node>> supplier, Predicate<ContainerStickerElement<?>> parentValidator) {
        this.name = name;
        this.supplier = supplier;
        this.parentValidator = parentValidator;
    }

    public String getName() {
        return name.get();
    }

    public Supplier<StickerElement<? extends Node>> getSupplier() {
        return supplier;
    }

    public boolean isParentValid(ContainerStickerElement<?> container) {
        return parentValidator.test(container);
    }
}
