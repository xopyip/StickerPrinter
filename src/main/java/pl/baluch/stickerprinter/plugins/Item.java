package pl.baluch.stickerprinter.plugins;

import javafx.scene.control.Alert;
import pl.baluch.stickerprinter.data.StickerProperty;
import pl.baluch.stickerprinter.data.Sticker;

import java.util.*;

public abstract class Item {
    private final Map<String, String> properties = new HashMap<>();

    private final Map<String, CustomPropertyType> customProperties = new HashMap<>();
    private final String name;
    private final String typeName;

    public Item(String name, String typeName) {
        this.name = name;
        this.typeName = typeName;
    }

    protected void addProperty(String key, String value) {
        properties.put(key, value);
    }

    protected void addCustomProperty(String name, CustomPropertyType type) {
        customProperties.put(name, type);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(name, item.name) && Objects.equals(properties, item.properties) && Objects.equals(customProperties, item.customProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, properties, customProperties);
    }

    public Sticker createSticker() {
        Sticker sticker = new Sticker();
        properties.forEach(sticker::addProperty);
        customProperties.forEach((k, v) -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Custom properties not implemented yet"); //todo
            alert.showAndWait();
        });
        return sticker;
    }

    public List<StickerProperty> getPreviewProperties() {
        List<StickerProperty> previewProperties = new ArrayList<>();
        properties.forEach((k, v) -> previewProperties.add(new StickerProperty(k, v)));
        customProperties.forEach((k, vt) -> previewProperties.add(new StickerProperty(k, "???")));
        return previewProperties;
    }

    public String getPropertyValue(String key){
        return properties.get(key);
    }

    public boolean isCustomProperty(String name) {
        return customProperties.containsKey(name);
    }

    public String getName() {
        return name;
    }

    public String getTypeName() {
        return typeName;
    }

    public enum CustomPropertyType {
        NUMBER,
        STRING
    }
}
