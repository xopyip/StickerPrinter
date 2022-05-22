package pl.baluch.stickerprinter.events;

import pl.baluch.stickerprinter.elements.StickerElement;

public record DeleteStickerElementEvent(StickerElement<?> stickerElement) {
}
