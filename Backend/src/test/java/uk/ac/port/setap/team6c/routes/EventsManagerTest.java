package uk.ac.port.setap.team6c.routes;

import com.google.gson.JsonObject;
import io.github.cdimascio.dotenv.DotenvEntry;
import io.javalin.http.Context;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import uk.ac.port.setap.team6c.Main;
import uk.ac.port.setap.team6c.database.DatabaseManager;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EventsManagerTest {

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
    
    insert into society (universityid, societyname, societydescription, societypicture) values
    (1, 'Test Society', 'Some description here', 'https://www.example.com/image.jpg');
    
    insert into events (date, location, name, description, price, image) values
    ('2025-06-13T12:24:01.849390400Z', 'Some Location', 'Event Name', 'Description', 10.2, 'https://www.example.com/image.jpg'),
    ('2025-06-15T12:24:01.849390400Z', 'Some Other Location', 'Name', 'Description', 5, 'https://www.example.com/image.jpg');
    
    insert into societyevent (societyid, eventid) values (1, 1), (1, 2);
    """
        );
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
            "id": 2
        }
        """);

        EventsManager.getAllEvents(ctx);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(ctx).result(captor.capture());
        String jsonResponse = captor.getValue();

        JsonObject[] events = Main.GSON.fromJson(jsonResponse, JsonObject[].class);

        assertEquals(0, events.length, "There should be 0 events for a society that doesn't exist");
    }

}
