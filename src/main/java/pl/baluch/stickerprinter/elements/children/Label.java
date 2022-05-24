package pl.baluch.stickerprinter.elements.children;

import com.google.gson.JsonObject;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import pl.baluch.stickerprinter.Storage;
import pl.baluch.stickerprinter.data.DrawContext;
import pl.baluch.stickerprinter.data.StickerElementProperty;
import pl.baluch.stickerprinter.data.StickerProperty;
import pl.baluch.stickerprinter.elements.StickerElement;
import pl.baluch.stickerprinter.plugins.Item;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Label extends StickerElement<javafx.scene.control.Label> {
    private final SimpleStringProperty text = new SimpleStringProperty("Test");
    private final SimpleIntegerProperty fontSize = new SimpleIntegerProperty(15);

    private WeakReference<javafx.scene.control.Label> nodeReference = new WeakReference<>(null);
    private WeakReference<DrawContext> contextReference = new WeakReference<>(null);

    public Label() {
        super(javafx.scene.control.Label::new);
        addProperty(StickerElementProperty.builder("Text")
                .value(text::get)
                .onChange(value -> {
                    if (value == null) {
                        return;
                    }
                    if (value.equals(Storage.getResourceBundle().getString("custom.property.name"))) {
                        TextInputDialog inputDialog = new TextInputDialog("");
                        inputDialog.setHeaderText(Storage.getResourceBundle().getString("custom.property.header"));
                        Optional<String> dialogOutput = inputDialog.showAndWait();
                        if(dialogOutput.isPresent()) {
                            value = dialogOutput.get();
                        }
                    }
                    text.set(value);
                    updateNode();
                })
                .setChoices(item -> {
                    List<String> strings = new ArrayList<>(
                            item.getPreviewProperties().stream()
                                    .map(StickerProperty::key)
                                    .map(s -> ":" + s)
                                    .toList()
                    );
                    strings.add(Storage.getResourceBundle().getString("custom.property.name"));
                    return strings;
                })
                .build());
        addProperty(StickerElementProperty.builder("Font size")
                .value(() -> fontSize.get() + "px")
                .onChange(value -> {
                    if (value == null || !value.endsWith("px")) {
                        return;
                    }
                    try {
                        int newValue = Integer.parseInt(value.replace("px", ""));
                        fontSize.set(newValue);
                        updateNode();
                    } catch (NumberFormatException ignored) {
                    }
                })
                .addChoices(Arrays.asList("5px", "10px", "15px", "20px", "25px", "30px", "35px", "40px", "45px", "50px", "55px", "60px"))
                .build());
    }

    private void updateNode() {
        Optional.ofNullable(nodeReference.get()).ifPresent(node -> {
            Optional.ofNullable(contextReference.get()).ifPresent(context -> updateText(node, context.item()));
            node.setFont(new Font(fontSize.get()));
        });
    }

    @Override
    public void draw(Pane pane, DrawContext drawContext) {
        javafx.scene.control.Label node = nodeSupplier.get();
        nodeReference = new WeakReference<>(node);
        contextReference = new WeakReference<>(drawContext);
        node.setFont(new Font(fontSize.get()));

        updateText(node, drawContext.item());

        node.setWrapText(false);
        super.bindBounds(node);
        super.setupNode(pane, node, drawContext);
        pane.getChildren().add(node);
    }

    private void updateText(javafx.scene.control.Label node, Item item) {
        if (text.get().startsWith(":")) {
            String key = text.get().substring(1);
            if (item.isCustomProperty(key)) {
                node.setText("???");
            } else {
                node.setText(item.getPropertyValue(key));
            }
        } else {
            node.setText(text.get());
        }
    }

    @Override
    public void deserialize(JsonObject properties) {
        if (properties.has("text"))
            text.set(properties.get("text").getAsString());
        if (properties.has("fontSize"))
            fontSize.set(properties.get("fontSize").getAsInt());
    }

    @Override
    public JsonObject serialize() {
        JsonObject properties = new JsonObject();
        properties.addProperty("text", text.get());
        properties.addProperty("fontSize", fontSize.get());
        return properties;
    }
}
