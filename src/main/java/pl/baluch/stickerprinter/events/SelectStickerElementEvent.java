package pl.baluch.stickerprinter.events;

import pl.baluch.stickerprinter.elements.StickerElement;

public record SelectStickerElementEvent(StickerElement<?> stickerElement) {
}
