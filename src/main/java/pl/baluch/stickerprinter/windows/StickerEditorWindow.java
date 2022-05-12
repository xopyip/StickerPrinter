package pl.baluch.stickerprinter.windows;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.baluch.stickerprinter.Storage;
import pl.baluch.stickerprinter.data.PageStyle;
import pl.baluch.stickerprinter.plugins.Item;

import java.io.IOException;

public class StickerEditorWindow extends Stage {
    public StickerEditorWindow(Item item, PageStyle pageStyle){

        super();

        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/stickerdesigneditor.fxml"));
        fxmlLoader.setController(new StickerEditorController(item, pageStyle));
        fxmlLoader.setResources(Storage.getResourceBundle());

        Parent parent = null;
        try {
            parent = fxmlLoader.load();
            StickerEditorController controller = fxmlLoader.getController();
            controller.setStage(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert parent != null;
        setScene(new Scene(parent));
    }
}
