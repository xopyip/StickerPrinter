package pl.baluch.stickerprinter.data;

import java.util.HashMap;
import java.util.Map;

public class Sticker {
    private final Map<String, String> properties = new HashMap<>();

    public void addProperty(String key, String value){
        properties.put(key, value);
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}
