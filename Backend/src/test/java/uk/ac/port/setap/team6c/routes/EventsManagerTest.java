package uk.ac.port.setap.team6c.routes;

import com.google.gson.JsonObject;
import io.github.cdimascio.dotenv.DotenvEntry;
import io.javalin.http.Context;
import io.javalin.http.OkResponse;
import io.javalin.http.UnauthorizedResponse;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import uk.ac.port.setap.team6c.Main;
import uk.ac.port.setap.team6c.database.DatabaseManager;

import java.lang.reflect.Field;
import java.sql.*;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EventsManagerTest {

    private static String token = null;
    private static Instant expiry = null;

    @BeforeAll
    static void setUp() throws IllegalAccessException, NoSuchFieldException, SQLException {
        for (DotenvEntry entry : Main.ENV.entries()) {
            if (entry.getKey().equals("DB_NAME")) {
                // Use reflection to modify the DB_NAME field of Main.ENV, allowing us to use the DatabaseManager
                // entirely unmodified to access a different database for testing
                Field valueField = entry.getClass().getDeclaredField("value");
                valueField.setAccessible(true);
                valueField.set(entry, "testdb");
            }
        }
        DatabaseManager.resetDatabase();

        Connection connection = DatabaseManager.getSource().getConnection();
        Statement statement = connection.createStatement();
        statement.execute("""
    insert into university (universityName, universityPicture, emailDomain, theming) values
    ('University of Portsmouth', 'https://upload.wikimedia.org/wikipedia/commons/d/dc/University_of_Portsmouth_Logo.png', '@port.ac.uk', '{"primarycolor":"#572985"}');
    
    insert into users (universityId, username, email, password, profilePicture) values
    (1, 'johndoe', 'johndoe@port.ac.uk', '$2y$10$wr1OF4PvzJX0nrfsJ6mumuriuI5MzNPdF.9nxzzElz2mldImt2n.O', 'https://api.dicebear.com/9.x/shapes/svg?seed=johndoe');
    
    insert into society (universityid, societyname, societydescription, societypicture) values
    (1, 'Test Society 1', 'Some description here', 'https://www.example.com/image.jpg'),
    (1, 'Test Society 2', 'Some description here', 'https://www.example.com/image.jpg'),
    (1, 'Test Society 3', 'Some description here', 'https://www.example.com/image.jpg');
    
    insert into societymember (userid, societyid) values (1, 1), (1, 2);
    
    insert into events (date, location, name, description, price, image) values
    ('2025-06-13T12:24:01.849390400Z', 'Some Location', 'Event Name', 'Description', 10.2, 'https://www.example.com/image.jpg'),
    ('2025-06-15T12:24:01.849390400Z', 'Some Other Location', 'Name', 'Description', 5, 'https://www.example.com/image.jpg');
    
    insert into societyevent (societyid, eventid) values (1, 1), (1, 2);
    """
        );
        statement.close();
        connection.close();

        // Log in as the user & get the token/expiry
        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn("""
            {
                "email": "johndoe@port.ac.uk",
                "password": "password"
            }
        """);
        AuthManager.login(ctx);
        connection = DatabaseManager.getSource().getConnection();
        statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select token, expiry from sessiontoken where userid = 1");
        if (resultSet.next()) {
            token = resultSet.getString("token");
            expiry = resultSet.getTimestamp("expiry").toInstant();
        }
        statement.close();
        connection.close();
    }

    @AfterAll
    static void tearDown() {
        DatabaseManager.resetDatabase();
    }

    @Test
    void testGetAllEvents() {
        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn("""
        {
            "id": 1
        }
        """);

        EventsManager.getAllEvents(ctx);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(ctx).result(captor.capture());
        String jsonResponse = captor.getValue();

        JsonObject[] events = Main.GSON.fromJson(jsonResponse, JsonObject[].class);

        assertEquals(2, events.length, "There should be 2 events for the society");
        assertEquals("Event Name", events[0].get("name").getAsString(), "First event name should match");
        assertEquals("Some Location", events[0].get("location").getAsString(), "First event location should match");
        assertEquals("Name", events[1].get("name").getAsString(), "Second event name should match");
        assertEquals("Some Other Location", events[1].get("location").getAsString(), "Second event location should match");
    }

    @Test
    void testGetAllEventsForEmptySociety() {
        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn("""
        {
            "id": 3
        }
        """);

        EventsManager.getAllEvents(ctx);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(ctx).result(captor.capture());
        String jsonResponse = captor.getValue();

        JsonObject[] events = Main.GSON.fromJson(jsonResponse, JsonObject[].class);

        assertEquals(0, events.length, "There should be 0 events for a society that has no events");
    }

    @Test
    void testGetAllEventsForNonexistentSociety() {
        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn("""
        {
            "id": 4
        }
        """);

        EventsManager.getAllEvents(ctx);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(ctx).result(captor.capture());
        String jsonResponse = captor.getValue();

        JsonObject[] events = Main.GSON.fromJson(jsonResponse, JsonObject[].class);

        assertEquals(0, events.length, "There should be 0 events for a society that doesn't exist");
    }

    @Test
    void testCreateEvent() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("societyId", 2);
        jsonObject.addProperty("token", token);
        jsonObject.addProperty("expiry", expiry.toString());
        jsonObject.addProperty("date", Instant.now().toString());
        jsonObject.addProperty("location", "Some location");
        jsonObject.addProperty("name", "Event name");
        jsonObject.addProperty("description", "A description");
        jsonObject.addProperty("price", 5);
        jsonObject.addProperty("image", "https://www.example.com/image.jpg");

        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn(jsonObject.toString());

        assertThrows(OkResponse.class, () -> EventsManager.createEvent(ctx), "Successfully creating an event" +
                "should throw OkResponse");
    }

    @Test
    void testCreateEventForSocietyTheUserHasNotJoined() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("societyId", 3);
        jsonObject.addProperty("token", token);
        jsonObject.addProperty("expiry", expiry.toString());
        jsonObject.addProperty("date", Instant.now().toString());
        jsonObject.addProperty("location", "Some location");
        jsonObject.addProperty("name", "Event name");
        jsonObject.addProperty("description", "A description");
        jsonObject.addProperty("price", 5);
        jsonObject.addProperty("image", "https://www.example.com/image.jpg");

        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn(jsonObject.toString());

        assertThrows(UnauthorizedResponse.class, () -> EventsManager.createEvent(ctx), "Trying to create an" +
                "event in a society the user has not joined should throw UnauthorizedResponse.");
    }

    @Test
    void testCreateEventWithInvalidToken() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("societyId", 2);
        jsonObject.addProperty("token", "e4b33986-5aa0-47e4-9036-fe939e5f5b8a");
        jsonObject.addProperty("expiry", expiry.toString());
        jsonObject.addProperty("date", Instant.now().toString());
        jsonObject.addProperty("location", "Some location");
        jsonObject.addProperty("name", "Event name");
        jsonObject.addProperty("description", "A description");
        jsonObject.addProperty("price", 5);
        jsonObject.addProperty("image", "https://www.example.com/image.jpg");

        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn(jsonObject.toString());

        assertThrows(UnauthorizedResponse.class, () -> EventsManager.createEvent(ctx), "Trying to create an" +
                "event without a valid token/expiry combination should throw UnauthorizedResponse.");
    }

    @Test
    void testCreateEventWithIncorrectTokenExpiry() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("societyId", 2);
        jsonObject.addProperty("token", token);
        jsonObject.addProperty("expiry", Instant.now().toString());
        jsonObject.addProperty("date", Instant.now().toString());
        jsonObject.addProperty("location", "Some location");
        jsonObject.addProperty("name", "Event name");
        jsonObject.addProperty("description", "A description");
        jsonObject.addProperty("price", 5);
        jsonObject.addProperty("image", "https://www.example.com/image.jpg");

        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn(jsonObject.toString());

        assertThrows(UnauthorizedResponse.class, () -> EventsManager.createEvent(ctx), "Trying to create an" +
                "event without a valid token/expiry combination should throw UnauthorizedResponse.");
    }

    @Test
    void testCreateEventInSocietyWhichDoesNotExist() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("societyId", 4);
        jsonObject.addProperty("token", token);
        jsonObject.addProperty("expiry", expiry.toString());
        jsonObject.addProperty("date", Instant.now().toString());
        jsonObject.addProperty("location", "Some location");
        jsonObject.addProperty("name", "Event name");
        jsonObject.addProperty("description", "A description");
        jsonObject.addProperty("price", 5);
        jsonObject.addProperty("image", "https://www.example.com/image.jpg");

        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn(jsonObject.toString());

        assertThrows(UnauthorizedResponse.class, () -> EventsManager.createEvent(ctx), "Trying to create an" +
                "event in a society which does not exist should throw UnauthorizedResponse.");
    }

}
