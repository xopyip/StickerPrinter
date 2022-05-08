package pl.baluch.pluginexample;

import pl.baluch.stickerprinter.plugins.Item;

public class ExampleShirtItem extends Item {

    public ExampleShirtItem(String name, String desc, String color) {
        super(name, "Shirt");
        addProperty("name", name);
        addProperty("desc", desc);
        addProperty("color", color);
        addCustomProperty("amount", CustomPropertyType.NUMBER);
    }
}
