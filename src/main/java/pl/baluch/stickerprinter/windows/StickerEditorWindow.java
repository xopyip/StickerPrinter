package pl.baluch.stickerprinter.windows;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.baluch.stickerprinter.AppMain;
import pl.baluch.stickerprinter.Storage;
import pl.baluch.stickerprinter.data.PageStyle;
import pl.baluch.stickerprinter.plugins.Item;

import java.io.IOException;

public class StickerEditorWindow extends Stage {
    public StickerEditorWindow(Item item, PageStyle pageStyle){

        super();

        StickerEditorController controller = new StickerEditorController(item, pageStyle);
        controller.setStage(this);

        AppMain.EVENT_BUS.register(controller);
        this.setOnCloseRequest(event -> AppMain.EVENT_BUS.unregister(controller));

        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/stickerdesigneditor.fxml"));
        fxmlLoader.setController(controller);
        fxmlLoader.setResources(Storage.getResourceBundle());

        Parent parent = null;
        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert parent != null;
        setScene(new Scene(parent));
    }
}
