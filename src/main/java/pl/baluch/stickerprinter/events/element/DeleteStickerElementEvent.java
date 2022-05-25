package pl.baluch.stickerprinter.events.element;

import pl.baluch.stickerprinter.elements.StickerElement;

public record DeleteStickerElementEvent(StickerElement<?> stickerElement) {
}
