package pl.baluch.pluginexample;

import pl.baluch.stickerprinter.plugins.Item;

public class ExampleShoesItem extends Item {
    private final String name;

    public ExampleShoesItem(String name, String desc, String color, int size) {
        super("Shoes");
        this.name = name;
        addProperty("name", name);
        addProperty("desc", desc);
        addProperty("color", color);
        addProperty("size", String.valueOf(size));
        addCustomProperty("amount", CustomPropertyType.NUMBER);
    }

    @Override
    protected String getName() {
        return name;
    }
}
