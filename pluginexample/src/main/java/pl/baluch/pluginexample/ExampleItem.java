package pl.baluch.pluginexample;

import pl.baluch.stickerprinter.plugins.Item;

public class ExampleItem extends Item {
    private final String name;

    public ExampleItem(String name, String desc, String color) {
        super();
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
