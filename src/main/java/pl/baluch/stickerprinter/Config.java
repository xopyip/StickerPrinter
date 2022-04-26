package pl.baluch.stickerprinter;

import pl.baluch.stickerprinter.data.PageStyle;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private Language locale = Language.English;
    private List<PageStyle> pageStyles = new ArrayList<>();

    public Language getLocale() {
        return locale;
    }

    public void setLocale(Language locale) {
        this.locale = locale;
    }

    public List<PageStyle> getPageStyles() {
        return pageStyles;
    }
}
