package pl.baluch.stickerprinter.elements.children;

import com.google.gson.JsonObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.Pane;
import pl.baluch.stickerprinter.elements.StickerElement;

public class Label extends StickerElement<javafx.scene.control.Label> {
    private SimpleStringProperty text = new SimpleStringProperty("Test");
    public Label() {
        super(new javafx.scene.control.Label());
        node.textProperty().bindBidirectional(text);
        node.setWrapText(false);
    }

    @Override
    public void draw(Pane pane) {
        super.setupPositionAndSize(node);
        super.setupNode(pane, node);
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
