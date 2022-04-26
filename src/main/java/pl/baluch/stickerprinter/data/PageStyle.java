package pl.baluch.stickerprinter.data;

import pl.baluch.stickerprinter.Storage;

public class PageStyle {
    private String name;

    public PageStyle(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static class New extends PageStyle {
        public New() {
            super(Storage.getResourceBundle().getString("print.newpagestyle"));
        }
    }
}
