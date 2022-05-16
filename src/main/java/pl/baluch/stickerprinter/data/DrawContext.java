package pl.baluch.stickerprinter.data;

import pl.baluch.stickerprinter.plugins.Item;

public record DrawContext(Item item, boolean isEditor) {
}
