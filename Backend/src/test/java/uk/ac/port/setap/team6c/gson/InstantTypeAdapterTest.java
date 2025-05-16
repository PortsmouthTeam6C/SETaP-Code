package uk.ac.port.setap.team6c.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class InstantTypeAdapterTest {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
            .create();

    @Test
    void testSerialize() {
        Instant now = Instant.now();
        String json = gson.toJson(now, Instant.class);
        assertEquals("\"" + now.toString() + "\"", json, "Serialization should return the correct ISO-8601 date string");
    }

    @Test
    void testDeserialize() {
        Instant now = Instant.now();
        String json = "\"" + now.toString() + "\"";
        Instant deserialized = gson.fromJson(json, Instant.class);
        assertEquals(now, deserialized, "Deserialization should return the correct Instant object");
    }

    @Test
    void testSerializationDeserialization() {
        Instant now = Instant.now();
        String serialized = gson.toJson(now, Instant.class);
        Instant deserialized = gson.fromJson(serialized, Instant.class);
        assertEquals(now, deserialized, "Serialization followed by deserialization should return the original Instant");
    }

}