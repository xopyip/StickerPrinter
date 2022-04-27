package pl.baluch.stickerprinter;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class Storage {
    private static final String SEP = System.getProperty("file.separator");
    private static File dir = new File(System.getProperty("user.home") + SEP + ".stickerprinter");
    private static Config config = null;

    public static Config getConfig() {
        if (config != null) {
            return config;
        }
        dir.mkdirs();
        Gson gson = new Gson();
        try (FileReader fileReader = new FileReader(new File(dir, "config.json"))) {
            return config = gson.fromJson(fileReader, Config.class);
        } catch (FileNotFoundException e) {
            return config = new Config();
        } catch (JsonSyntaxException | JsonIOException | IOException e) {
            System.err.println("Config loading problem:");
            e.printStackTrace();
            //todo: backup config and display warning message
            return config = new Config();
        }
    }

    public static void saveConfig() {
        if (config == null) {
            return;
        }
        Gson gson = new Gson();
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

    public static File getPluginsDir(){
        return new File(dir, "plugins");
    }

    public static File[] listPluginFiles(){
        File pluginsDir = getPluginsDir();
        pluginsDir.mkdirs();
        return pluginsDir.listFiles();
    }
}
