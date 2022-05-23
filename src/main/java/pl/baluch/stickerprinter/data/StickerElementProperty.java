package pl.baluch.stickerprinter.data;

import java.util.*;
import java.util.function.Consumer;

public final class StickerElementProperty {
    private final String key;
    private final String value;
    private final Consumer<String> onChange;
    private Collection<String> choices;

    private StickerElementProperty(String key, String value, Consumer<String> onChange, List<String> choices) {
        this.key = key;
        this.value = value;
        this.onChange = onChange;
        this.choices = choices;
    }

    public static Builder builder(String text) {
        return new Builder(text);
    }

    public Collection<String> getValues() {
        return choices;
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

    public Consumer<String> onChange() {
        return onChange;
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
        private String key;
        private String value;
        private Consumer<String> onChange;
        private List<String> choices = new ArrayList<>();

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
            return new StickerElementProperty(key, value, onChange, choices);
        }

        public Builder addChoice(String s) {
            choices.add(s);
            return this;
        }

        public Builder addChoices(Collection<String> choices) {
            this.choices.addAll(choices);
            return this;
        }
    }
}
