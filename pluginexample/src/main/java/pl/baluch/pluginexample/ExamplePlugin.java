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
        addShirts(items);
        addShoes(items);
        return items;
    }

    private void addShoes(Set<Item> items) {
        items.add(new ExampleShoesItem("Test shoes product", "Description of test product", "red", 44));
        items.add(new ExampleShoesItem("Another shoes product", "Second description", "green", 40));
        items.add(new ExampleShoesItem("Last shoes product", "Third description", "blue", 38));
    }

    private void addShirts(Set<Item> items) {
        items.add(new ExampleShirtItem("Test product", "Description of test product", "red"));
        items.add(new ExampleShirtItem("Another test product", "Second description", "green"));
        items.add(new ExampleShirtItem("Last test product", "Third description", "blue"));
    }
}
