package uk.ac.port.setap.team6c.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Instant;

/**
 * A Gson TypeAdapter for converting Instant objects to and from JSON
 */
public class InstantTypeAdapter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {

    /**
     * Serialize an Instant object to JSON
     * @param src the Instant object to serialize
     * @param typeOfSrc ignored
     * @param context ignored
     * @return a serialized JsonElement
     */
    @Override
    public JsonElement serialize(Instant src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());  // Store as ISO-8601 String
    }

    /**
     * Deserialize an Instant object from JSON
     * @param json The JsonElement to deserialize
     * @param typeOfT ignored
     * @param context ignored
     * @return the deserialized Instant object
     */
    @Override
    public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Instant.parse(json.getAsString());  // Parse back to Instant
    }

}
