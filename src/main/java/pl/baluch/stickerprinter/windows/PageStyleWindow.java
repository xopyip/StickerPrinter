package pl.baluch.stickerprinter.windows;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.baluch.stickerprinter.Storage;
import pl.baluch.stickerprinter.data.PageStyle;

import java.io.IOException;

public class PageStyleWindow extends Stage {

    private PageStyle pageStyle;

    public PageStyleWindow() {
        super();

        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/pagestyleeditor.fxml"));
        fxmlLoader.setResources(Storage.getResourceBundle());

        Parent parent = null;
        try {
            parent = fxmlLoader.load();
            PageStyleController controller = fxmlLoader.getController();
            controller.setExitHandler(pageStyle -> {
                this.pageStyle = pageStyle;
                PageStyleWindow.this.close();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert parent != null;
        setScene(new Scene(parent));
    }

    public PageStyle createPageStyle() {
        showAndWait();
        return pageStyle;
    }
}
