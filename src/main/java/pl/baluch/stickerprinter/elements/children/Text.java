package pl.baluch.stickerprinter.elements.children;

import com.google.gson.JsonObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextFlow;
import pl.baluch.stickerprinter.elements.StickerElement;

public class Text extends StickerElement<TextFlow> {
    private final SimpleStringProperty text = new SimpleStringProperty("Test");
    public Text() {
        super(new TextFlow(new javafx.scene.text.Text()));
        //todo: reimplemnt this as block
    }

    @Override
    public void draw(Pane pane) {
        super.setupPositionAndSize(node);
        super.setupNode(pane, node);
        pane.getChildren().add(node);
    }

    @Override
    public void deserialize(JsonObject properties) {

    }

    @Override
    public JsonObject serialize() {
        return null;
    }
}
