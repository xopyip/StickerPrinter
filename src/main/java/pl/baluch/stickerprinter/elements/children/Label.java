package pl.baluch.stickerprinter.elements.children;

import com.google.gson.JsonObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.Pane;
import pl.baluch.stickerprinter.data.DrawContext;
import pl.baluch.stickerprinter.data.StickerElementProperty;
import pl.baluch.stickerprinter.elements.StickerElement;

public class Label extends StickerElement<javafx.scene.control.Label> {
    private final SimpleStringProperty text = new SimpleStringProperty("Test");
    public Label() {
        super(javafx.scene.control.Label::new);
        addProperty(StickerElementProperty.builder("Text")
                .value(text.get())
                .onChange(value -> {
                    if(value == null){
                        return;
                    }
                    if(value.equals("Custom...")){
                        throw new RuntimeException("Custom text is not supported yet");
                    }
                    text.set(value);
                })
                //.addChoices() //todo: list all props from item
                .addChoice("Custom...")
                .build());
    }

    @Override
    public void draw(Pane pane, DrawContext drawContext) {
        javafx.scene.control.Label node = nodeSupplier.get();
        node.textProperty().bindBidirectional(text);
        node.setWrapText(false);
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
