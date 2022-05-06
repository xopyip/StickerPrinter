package pl.baluch.stickerprinter.plugins;

import javafx.scene.control.Alert;
import pl.baluch.stickerprinter.data.Sticker;

import java.util.HashMap;
import java.util.Map;

public abstract class Item {
    private final Map<String, String> properties = new HashMap<>();

    private final Map<String, CustomPropertyType> customProperties = new HashMap<>();
    private String category;

    public Item(String category){

        this.category = category;
    }
    protected void addProperty(String key, String value){
        properties.put(key, value);
    }

    protected void addCustomProperty(String name, CustomPropertyType type) {
        customProperties.put(name, type);
    }

    @Override
    public String toString() {
        return getName();
    }

    protected abstract String getName();

    public String getCategory() {
        return category;
    }

    public Sticker createSticker(){
        Sticker sticker = new Sticker();
        properties.forEach(sticker::addProperty);
        customProperties.forEach((k, v) -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Custom properties not implemented yet"); //todo
            alert.showAndWait();
        });
        return sticker;
    }

    public enum CustomPropertyType{
        NUMBER,
        STRING;
    }
}
