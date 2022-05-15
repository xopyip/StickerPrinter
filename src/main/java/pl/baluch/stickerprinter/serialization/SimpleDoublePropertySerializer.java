package pl.baluch.stickerprinter.serialization;

import com.google.gson.*;
import javafx.beans.property.SimpleDoubleProperty;

import java.lang.reflect.Type;

public class SimpleDoublePropertySerializer implements JsonSerializer<SimpleDoubleProperty>, JsonDeserializer<SimpleDoubleProperty> {
    @Override
    public SimpleDoubleProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return new SimpleDoubleProperty(json.getAsDouble());
    }

    @Override
    public JsonElement serialize(SimpleDoubleProperty src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.get());
    }
}
