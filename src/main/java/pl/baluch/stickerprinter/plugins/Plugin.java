package pl.baluch.stickerprinter.plugins;

import java.util.Set;

public interface Plugin {
    String getName();
    Set<Item> getItems();
}
