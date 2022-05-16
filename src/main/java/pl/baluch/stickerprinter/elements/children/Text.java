package pl.baluch.stickerprinter.elements.children;

import com.google.gson.JsonObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.Pane;
import pl.baluch.stickerprinter.data.DrawContext;
import pl.baluch.stickerprinter.elements.StickerElement;


public class Text extends StickerElement<javafx.scene.text.Text> {
    private final SimpleStringProperty text = new SimpleStringProperty("Test");

    public Text() {
        super(new javafx.scene.text.Text());
        node.textProperty().bindBidirectional(text);
    }

    @Override
    public void draw(Pane pane, DrawContext drawContext) {
        super.setupPositionAndSize(node);
        super.setupNode(pane, node, drawContext);
        pane.getChildren().add(node);
    }

    @Override
    public void deserialize(JsonObject properties) {
        text.set(properties.get("text").getAsString());
    }

    @Override
    public JsonObject serialize() {
        JsonObject properties = new JsonObject();
        properties.addProperty("text", text.get());
        return properties;
    }
}
