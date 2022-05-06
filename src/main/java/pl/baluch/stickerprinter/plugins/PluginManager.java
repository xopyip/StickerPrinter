package pl.baluch.stickerprinter.plugins;

import pl.baluch.stickerprinter.Storage;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public static PluginManager getInstance() {
        if (instance == null) {
            instance = new PluginManager();
        }
        return instance;
    }

    public void addChangeListener(PropertyChangeListener listener){
        support.addPropertyChangeListener(listener);
    }

    public void load() throws IOException, InstantiationException, IllegalAccessException {
        for (File file : Storage.listPluginFiles()) {
            if (file.getName().endsWith(".jar")) {
                loadInternal(file).ifPresent(plugin -> support.firePropertyChange("plugins", null, plugin));
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
        support.firePropertyChange("plugins", plugin, null);
    }

    private Optional<Plugin> loadInternal(File file) throws IOException, InstantiationException, IllegalAccessException {
        URLClassLoader child = new URLClassLoader(
                new URL[]{file.toURI().toURL()},
                this.getClass().getClassLoader()
        );
        try (ZipFile zipFile = new ZipFile(file)) {
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
                Constructor<?> constructor = pluginMain.get().getDeclaredConstructor();
                Plugin plugin = (Plugin) constructor.newInstance();
                System.out.println("Loaded plugin: " + plugin.getName());
                pluginList.put(plugin, new PluginInfo(file, child));
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

        plugin.ifPresent(value -> support.firePropertyChange("plugins", null, value));

        return plugin;
    }

    private Stream<Item> getItemsStream() {
        return pluginList.keySet().stream()
                .flatMap(plugin -> plugin.getItems().stream());
    }

    public List<String> getCategories() {
        List<String> collect = getItemsStream().map(Item::getCategory).distinct().collect(Collectors.toList());
        collect.add(0, Storage.getResourceBundle().getString("items.all"));
        return collect;
    }

    public List<Item> getItems(String category){
        if(Objects.equals(category, Storage.getResourceBundle().getString("items.all"))){
            return getItemsStream().collect(Collectors.toList());
        }
        return getItemsStream().filter(item -> item.getCategory().equals(category)).collect(Collectors.toList());
    }
}
