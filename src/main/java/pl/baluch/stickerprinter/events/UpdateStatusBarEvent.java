package pl.baluch.stickerprinter.events;

import pl.baluch.stickerprinter.data.StatusBar;

public record UpdateStatusBarEvent(StatusBar side, String text) {
}
