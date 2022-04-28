package pl.baluch.pluginexample;

import pl.baluch.stickerprinter.plugins.Item;
import pl.baluch.stickerprinter.plugins.Plugin;

import java.util.HashSet;
import java.util.Set;

public class ExamplePlugin implements Plugin {

    @Override
    public String getName() {
        return "ExamplePlugin";
    }

    @Override
    public Set<Item> getItems() {
        Set<Item> items = new HashSet<>();
        items.add(new ExampleItem("Test product", "Description of test product", "red"));
        items.add(new ExampleItem("Another test product", "Second description", "green"));
        items.add(new ExampleItem("Last test product", "Third description", "blue"));
        return items;
    }
}
