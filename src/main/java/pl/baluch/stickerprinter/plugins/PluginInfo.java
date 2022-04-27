package pl.baluch.stickerprinter.plugins;

import java.io.File;
import java.net.URLClassLoader;

public class PluginInfo {
    private final File file;
    private final URLClassLoader classLoader;

    public PluginInfo(File file, URLClassLoader classLoader) {
        this.file = file;
        this.classLoader = classLoader;
    }

    public File getFile() {
        return file;
    }

    public URLClassLoader getClassLoader() {
        return classLoader;
    }
}
