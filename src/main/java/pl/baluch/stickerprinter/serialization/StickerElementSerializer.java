package pl.baluch.stickerprinter.serialization;

import com.google.gson.*;
import javafx.scene.Node;
import pl.baluch.stickerprinter.elements.ContainerStickerElement;
import pl.baluch.stickerprinter.elements.StickerElement;
import pl.baluch.stickerprinter.elements.StickerElementTypes;

import java.lang.reflect.Type;

public class StickerElementSerializer implements JsonSerializer<StickerElement<? extends Node>>, JsonDeserializer<StickerElement<? extends Node>> {
    @Override
    public StickerElement<? extends Node> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject asJsonObject = json.getAsJsonObject();
        String type = json.getAsJsonObject().get("type").getAsString();
        StickerElementTypes stickerElementTypes = StickerElementTypes.valueOf(type);
        StickerElement<? extends Node> stickerElement = stickerElementTypes.getSupplier().get();
        stickerElement.setX(asJsonObject.get("x").getAsDouble());
        stickerElement.setY(asJsonObject.get("y").getAsDouble());
        stickerElement.setWidth(asJsonObject.get("width").getAsDouble());
        stickerElement.setHeight(asJsonObject.get("height").getAsDouble());
        stickerElement.deserialize(asJsonObject.getAsJsonObject("properties"));
        if (stickerElement instanceof ContainerStickerElement && asJsonObject.has("children")) {
            asJsonObject.get("children").getAsJsonArray().forEach(child -> {
                ((ContainerStickerElement) stickerElement).addChild(deserialize(child, typeOfT, context));
            });
        }
        return stickerElement;
    }

    @Override
    public JsonElement serialize(StickerElement<? extends Node> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("x", src.getX());
        jsonObject.addProperty("y", src.getY());
        jsonObject.addProperty("width", src.getWidth());
        jsonObject.addProperty("height", src.getHeight());
        jsonObject.add("properties", src.serialize());
        jsonObject.addProperty("type", src.getType().name());
        if (src instanceof ContainerStickerElement) {
            JsonArray children = new JsonArray();
            ((ContainerStickerElement<?>) src).getChildren().forEach(child -> {
                children.add(serialize(child, typeOfSrc, context));
            });
            jsonObject.add("children", children);
        }
        return jsonObject;
    }
}
