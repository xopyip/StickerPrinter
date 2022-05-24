package pl.baluch.stickerprinter.data;

import pl.baluch.stickerprinter.plugins.Item;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public final class StickerElementProperty {
    private final String key;
    private final String value;
    private final Consumer<String> onChange;
    private final Function<Item, Collection<String>> choicesFunction;

    private StickerElementProperty(String key, String value, Consumer<String> onChange, List<String> choices) {
        this.key = key;
        this.value = value;
        this.onChange = onChange;
        choicesFunction = item -> choices;
    }
    private StickerElementProperty(String key, String value, Consumer<String> onChange, Function<Item, Collection<String>> choicesFunction) {
        this.key = key;
        this.value = value;
        this.onChange = onChange;
        this.choicesFunction = choicesFunction;
    }

    public static Builder builder(String text) {
        return new Builder(text);
    }

    public Collection<String> getValues(Item item) {
        return choicesFunction.apply(item);
    }

    public void update(String newValue) {
        onChange.accept(newValue);
    }

    public String key() {
        return key;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (StickerElementProperty) obj;
        return Objects.equals(this.key, that.key) &&
                Objects.equals(this.value, that.value) &&
                Objects.equals(this.onChange, that.onChange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value, onChange);
    }

    @Override
    public String toString() {
        return "StickerElementProperty[" +
                "key=" + key + ", " +
                "value=" + value + ", " +
                "onChange=" + onChange + ']';
    }


    public static class Builder {
        private final String key;
        private String value;
        private Consumer<String> onChange;
        private final List<String> choices = new ArrayList<>();
        private Function<Item, Collection<String>> choicesFunction;

        public Builder(String key) {
            this.key = key;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder onChange(Consumer<String> onChange) {
            this.onChange = onChange;
            return this;
        }

        public StickerElementProperty build() {
            if(choicesFunction != null){
                return new StickerElementProperty(key, value, onChange, choicesFunction);
            }
            return new StickerElementProperty(key, value, onChange, choices);
        }

        public Builder addChoice(String s) {
            if(choicesFunction != null){
                throw new IllegalStateException("Choices function already set");
            }
            choices.add(s);
            return this;
        }

        public Builder addChoices(Collection<String> choices) {
            if(choicesFunction != null){
                throw new IllegalStateException("Choices function already set");
            }
            this.choices.addAll(choices);
            return this;
        }

        public Builder setChoices(Function<Item, Collection<String>> o) {
            if(choices.size() > 0){
                throw new IllegalStateException("Choices already set");
            }
            this.choicesFunction = o;
            return this;
        }
    }
}
