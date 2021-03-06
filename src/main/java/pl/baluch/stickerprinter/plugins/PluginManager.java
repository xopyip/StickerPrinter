package pl.baluch.stickerprinter.plugins;

import pl.baluch.stickerprinter.AppMain;
import pl.baluch.stickerprinter.Storage;
import pl.baluch.stickerprinter.events.plugin.PluginLoadEvent;
import pl.baluch.stickerprinter.events.plugin.PluginUnloadEvent;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
                loadInternal(file).ifPresent(plugin -> AppMain.EVENT_BUS.post(new PluginLoadEvent(plugin)));
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
        AppMain.EVENT_BUS.post(new PluginUnloadEvent(plugin));
    }

    private Optional<Plugin> loadInternal(File file) throws IOException, InstantiationException, IllegalAccessException {
        URLClassLoader child = new URLClassLoader(
                new URL[]{file.toURI().toURL()},
                this.getClass().getClassLoader()
        );
        try (ZipFile zipFile = new ZipFile(file)) {
            Optional<? extends Class<?>> pluginMain = zipFile.stream()
                    .filter(entry -> entry.getName().endsWith(".class"))
                    .filter(entry -> !entry.getName().equalsIgnoreCase("module-info.class"))
                    .map(entry -> entry.getName().replace(".class", "").replace("/", "."))
                    .map(className -> {
                        try {
                            return child.loadClass(className);
                        } catch (ClassNotFoundException ignored) {
                        }
                        return null;
                    }).filter(pluginClass -> pluginClass != null && Plugin.class.isAssignableFrom(pluginClass))
                    .findFirst();
            if (pluginMain.isPresent()) {
                Constructor<?> constructor = pluginMain.get().getDeclaredConstructor();
                Plugin plugin = (Plugin) constructor.newInstance();
                System.out.println("Loaded plugin: " + plugin.getName());
                pluginList.put(plugin, new PluginInfo(file, child));
                plugin.addChangeListener(evt -> AppMain.EVENT_BUS.post(new PluginLoadEvent(plugin)));
                return Optional.of(plugin);
            } else {
                System.err.println("Plugin without main class: " + file.getName());
            }
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            System.err.println("Plugin must have constructor without arguments: " + file.getName());
            return Optional.empty();
        }
        return Optional.empty();
    }

    public Optional<Plugin> loadExternal(File file) throws IOException, InstantiationException, IllegalAccessException {
        File target = new File(Storage.getPluginsDir(), file.getName());
        Files.copy(file.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Optional<Plugin> plugin = loadInternal(target);

        plugin.ifPresent(value -> AppMain.EVENT_BUS.post(new PluginLoadEvent(value)));

        return plugin;
    }

    //todo: cache per plugin, on update we have information about updated plugin so we can reload items from specific plugin only
    private Stream<Item> getAllItemsStream() {
        return pluginList.keySet().stream()
                .flatMap(plugin -> plugin.getItems().stream());
    }
    private Stream<Item> getItemsStream(String category) {
        return pluginList.keySet().stream()
                .flatMap(plugin -> plugin.getItems(category).stream());
    }

    int i = 0;
    public List<String> getCategories() {
        List<String> collect = pluginList.keySet().stream().flatMap(plugin -> plugin.getCategories().stream()).distinct().sorted(String::compareToIgnoreCase).collect(Collectors.toList());
        collect.add(0, Storage.getResourceBundle().getString("items.all"));
        return collect;
    }

    public List<Item> getItems(String category){
        if(Objects.equals(category, Storage.getResourceBundle().getString("items.all"))){
            return getAllItemsStream().sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName())).collect(Collectors.toList());
        }
        return getItemsStream(category).collect(Collectors.toList());
    }

    public void onClose() {
        pluginList.forEach((k,v) -> k.setExit());
    }
}
