package pl.baluch.stickerprinter;

import pl.baluch.stickerprinter.data.Language;
import pl.baluch.stickerprinter.data.PageStyle;
import pl.baluch.stickerprinter.data.StickerDesign;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Config {
    private Language locale = Language.English;
    private final List<PageStyle> pageStyles = new ArrayList<>(Arrays.asList(
            new PageStyle("1x1", 1, 1, 0, 0),
            new PageStyle("2x2", 2, 2, 0, 0),
            new PageStyle("2x4", 2, 4, 0, 0)
    ));

    private final List<StickerDesign> stickerDesigns = new ArrayList<>();

    public Language getLocale() {
        return locale;
    }

    public void setLocale(Language locale) {
        this.locale = locale;
    }

    public List<PageStyle> getPageStyles() {
        return pageStyles;
    }

    public List<StickerDesign> getStickerDesigns() {
        return stickerDesigns;
    }

    public Optional<StickerDesign> getStickerDesign(float cellRatio, String typeName) {
        return stickerDesigns.stream()
                .filter(design -> design.matches(cellRatio, typeName))
                .findFirst();
    }
}
