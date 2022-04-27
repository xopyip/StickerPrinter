package pl.baluch.stickerprinter.plugins;

import pl.baluch.stickerprinter.Storage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.zip.ZipFile;

public class PluginManager {
    private static PluginManager instance;
    private final Map<Plugin, PluginInfo> pluginList = new HashMap<>();

    public static PluginManager getInstance() {
        if (instance == null) {
            instance = new PluginManager();
        }
        return instance;
    }

    public void load() throws IOException, InstantiationException, IllegalAccessException {
        for (File file : Storage.listPluginFiles()) {
            if (file.getName().endsWith(".jar")) {
                loadInternal(file);
            } else {
                System.err.println("Invalid file in plugins dir: " + file.getName());
            }
        }
    }

    public Collection<Plugin> getPlugins() {
        return pluginList.keySet();
    }

    public void unload(Plugin plugin) throws IOException {
        PluginInfo pluginInfo = pluginList.get(plugin);
        pluginList.remove(plugin);
        pluginInfo.getClassLoader().close();
        pluginInfo.getFile().delete();
    }

    private Optional<Plugin> loadInternal(File file) throws IOException, InstantiationException, IllegalAccessException {
        URLClassLoader child = new URLClassLoader(
                new URL[]{file.toURI().toURL()},
                this.getClass().getClassLoader()
        );
        ZipFile zipFile = new ZipFile(file);
        Optional<? extends Class<?>> pluginMain = zipFile.stream()
                .filter(entry -> entry.getName().endsWith(".class"))
                .map(entry -> entry.getName().replace(".class", "").replace("/", "."))
                .map(className -> {
                    try {
                        return child.loadClass(className);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).filter(pluginClass -> pluginClass != null && Plugin.class.isAssignableFrom(pluginClass))
                .findFirst();
        if (pluginMain.isPresent()) {
            Plugin plugin = (Plugin) pluginMain.get().newInstance();
            System.out.println("Loaded plugin: " + plugin.getName());
            pluginList.put(plugin, new PluginInfo(file, child));
            return Optional.of(plugin);
        } else {
            System.err.println("Plugin without main class: " + file.getName());
        }
        return Optional.empty();
    }

    public Optional<Plugin> loadExternal(File file) throws IOException, InstantiationException, IllegalAccessException {
        File target = new File(Storage.getPluginsDir(), file.getName());
        Files.copy(file.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return loadInternal(target);
    }
}
