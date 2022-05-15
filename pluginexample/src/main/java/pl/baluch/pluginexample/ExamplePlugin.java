package pl.baluch.pluginexample;

import pl.baluch.stickerprinter.plugins.Item;
import pl.baluch.stickerprinter.plugins.ItemsSupplier;
import pl.baluch.stickerprinter.plugins.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ExamplePlugin extends Plugin {

    private final List<Item> autoGenItems = new ArrayList<>();

    public ExamplePlugin() {
        super("ExamplePlugin");


        Thread thread = new Thread(this::updateThread);
        thread.setDaemon(false);
        thread.start();
    }

    private void updateThread() {
        int i = 0;
        while (!this.exit) {
            if (i > 5) {
                autoGenItems.removeIf(item -> item instanceof ExampleShoesItem && item.getName().equals("Product added on the fly"));
                i = 0;
            } else {
                autoGenItems.add(new ExampleShoesItem("Product added on the fly", "Third description", "blue", ThreadLocalRandom.current().nextInt(36, 45)));
            }
            i++;
            updateProducts("AutoGen");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @ItemsSupplier(category="AutoGen", visibleInAll = false)
    public List<Item> getAutoGen() {
        return autoGenItems;
    }
    @ItemsSupplier(category="Shoes")
    public List<Item> getShoes() {
        List<Item> items = new ArrayList<>();
        items.add(new ExampleShoesItem("Test shoes product", "Description of test product", "red", 44));
        items.add(new ExampleShoesItem("Another shoes product", "Second description", "green", 40));
        items.add(new ExampleShoesItem("Last shoes product", "Third description", "blue", 38));
        return items;
    }

    @ItemsSupplier(category="Shirts")
    public List<Item> addShirts() {
        List<Item> items = new ArrayList<>();
        items.add(new ExampleShirtItem("Test product", "Description of test product", "red"));
        items.add(new ExampleShirtItem("Another test product", "Second description", "green"));
        items.add(new ExampleShirtItem("Last test product", "Third description", "blue"));
        return items;
    }
}
