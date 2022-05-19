package pl.baluch.stickerprinter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import javafx.beans.property.SimpleDoubleProperty;
import pl.baluch.stickerprinter.data.StickerDesign;
import pl.baluch.stickerprinter.elements.StickerElement;
import pl.baluch.stickerprinter.elements.containers.StickerAnchorPane;
import pl.baluch.stickerprinter.plugins.Item;
import pl.baluch.stickerprinter.serialization.SimpleDoublePropertySerializer;
import pl.baluch.stickerprinter.serialization.StickerElementSerializer;

import java.io.*;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class Storage {
    private static final String SEP = System.getProperty("file.separator");
    private static final File dir = new File(System.getProperty("user.home") + SEP + ".stickerprinter");
    private static Config config = null;
    private static Gson gson;

    public static Config getConfig() {
        if (config != null) {
            return config;
        }
        dir.mkdirs();
        Gson gson = getGson();
        try (FileReader fileReader = new FileReader(new File(dir, "config.json"))) {
            config = gson.fromJson(fileReader, Config.class);
            if (config == null) {
                config = new Config();
            }
        } catch (FileNotFoundException e) {
            config = new Config();
        } catch (JsonSyntaxException | JsonIOException | IOException e) {
            System.err.println("Config loading problem:");
            e.printStackTrace();
            //todo: backup config and display warning message
            config = new Config();
        }
        return config;
    }

    public static void saveConfig() {
        if (config == null) {
            return;
        }
        Gson gson = getGson();
        try (FileWriter fileWriter = new FileWriter(new File(dir, "config.json"))) {
            fileWriter.write(gson.toJson(config));
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle("translations", new Locale(Storage.getConfig().getLocale().getCode()));
    }

    public static File getPluginsDir() {
        return new File(dir, "plugins");
    }

    public static File[] listPluginFiles() {
        File pluginsDir = getPluginsDir();
        pluginsDir.mkdirs();
        return pluginsDir.listFiles();
    }

    public static Gson getGson() {
        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder()
                    .registerTypeAdapter(StickerAnchorPane.class, new StickerElementSerializer())
                    .registerTypeAdapter(StickerElement.class, new StickerElementSerializer())
                    .registerTypeAdapter(SimpleDoubleProperty.class, new SimpleDoublePropertySerializer())
                    .setPrettyPrinting();
            return gsonBuilder.create();
        }
        return gson;
    }

    public static Optional<StickerDesign> matchStickerDesign(float ratio, Item selectedItem) {
        return Storage.getConfig().getStickerDesigns()
                .stream().filter(design -> design.matches(ratio, selectedItem.getTypeName())).findAny();
    }
}
