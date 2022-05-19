package pl.baluch.stickerprinter.events;

import pl.baluch.stickerprinter.plugins.Plugin;

public record PluginLoadEvent(Plugin value) {
}
