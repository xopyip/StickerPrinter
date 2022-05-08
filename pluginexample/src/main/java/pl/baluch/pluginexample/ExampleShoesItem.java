package pl.baluch.pluginexample;

import pl.baluch.stickerprinter.plugins.Item;

public class ExampleShoesItem extends Item {

    public ExampleShoesItem(String name, String desc, String color, int size) {
        super(name, "Shoes");
        addProperty("name", name);
        addProperty("desc", desc);
        addProperty("color", color);
        addProperty("size", String.valueOf(size));
        addCustomProperty("amount", CustomPropertyType.NUMBER);
    }
}
