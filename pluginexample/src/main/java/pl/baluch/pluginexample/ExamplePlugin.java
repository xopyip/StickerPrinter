package pl.baluch.pluginexample;

import pl.baluch.stickerprinter.plugins.Item;
import pl.baluch.stickerprinter.plugins.Plugin;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class ExamplePlugin extends Plugin {

    private Set<Item> items;

    public ExamplePlugin() {
        super("ExamplePlugin");

        items = new HashSet<>();
        addShirts(items);
        addShoes(items);

        Thread thread = new Thread(this::updateThread);
        thread.setDaemon(false);
        thread.start();
    }

    private void updateThread() {
        int i = 0;
        while (!this.exit) {
            if (i > 5) {
                items.removeIf(item -> item instanceof ExampleShoesItem && ((ExampleShoesItem) item).getName().equals("Product added on the fly"));
                i = 0;
            } else {
                items.add(new ExampleShoesItem("Product added on the fly", "Third description", "blue", ThreadLocalRandom.current().nextInt(36, 45)));
            }
            i++;
            updateProducts();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Set<Item> getItems() {
        return this.items;
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
