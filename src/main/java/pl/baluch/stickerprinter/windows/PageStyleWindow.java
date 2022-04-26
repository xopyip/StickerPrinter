package pl.baluch.stickerprinter.windows;

import javafx.stage.Stage;
import pl.baluch.stickerprinter.data.PageStyle;

public class PageStyleWindow extends Stage {
    public PageStyle getNewStyle() {
        return new PageStyle("Test");
    }
}
