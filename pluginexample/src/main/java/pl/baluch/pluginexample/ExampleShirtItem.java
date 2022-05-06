package pl.baluch.pluginexample;

import pl.baluch.stickerprinter.plugins.Item;

public class ExampleShirtItem extends Item {
    private final String name;

    public ExampleShirtItem(String name, String desc, String color) {
        super("Shirt");
        this.name = name;
        addProperty("name", name);
        addProperty("desc", desc);
        addProperty("color", color);
        addCustomProperty("amount", CustomPropertyType.NUMBER);
    }

    @Override
    protected String getName() {
        return name;
    }
}
