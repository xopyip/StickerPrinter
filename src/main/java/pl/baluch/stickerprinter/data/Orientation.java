package pl.baluch.stickerprinter.data;

import pl.baluch.stickerprinter.Storage;

import java.util.Locale;

public enum Orientation {
    HORIZONTAL,
    VERTICAL;

    @Override
    public String toString() {
        return Storage.getResourceBundle().getString("orientation." + name().toLowerCase(Locale.ROOT));
    }
}
