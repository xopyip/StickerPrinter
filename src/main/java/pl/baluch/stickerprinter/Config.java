package pl.baluch.stickerprinter;

import pl.baluch.stickerprinter.data.PageStyle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config {
    private Language locale = Language.English;
    private List<PageStyle> pageStyles = new ArrayList<>(Arrays.asList(
            new PageStyle("1x1", 1, 1, 0, 0),
            new PageStyle("2x2", 2, 2, 0, 0),
            new PageStyle("2x4", 2, 4, 0, 0)
    ));

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
