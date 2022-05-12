package pl.baluch.stickerprinter.elements;

import javafx.scene.layout.Pane;

import java.util.function.Supplier;

public abstract class StickerElement {
    protected float x = 0;
    protected float y = 0;
    protected float width = -1;
    protected float height = -1;

    public abstract void draw(Pane pane);

    public record Provider<T extends StickerElement>(String name, Supplier<T> supplier) {

        public T get() {
            return this.supplier.get();
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
