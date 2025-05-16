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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MessageManagerTest {

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
    
    insert into message (userid, societyid, content, timestamp) values
    (1, 1, 'Some message here', '2025-05-16 05:59:22'::timestamp at time zone 'UTC'),
    (1, 1, 'Another message here', '2025-05-16 06:00:03'::timestamp at time zone 'UTC');
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
    void testGetAllMessages() {
        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn("""
        {
            "id": 1
        }
        """);

        MessageManager.getAllMessages(ctx);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(ctx).result(captor.capture());
        String jsonResponse = captor.getValue();

        JsonObject[] messages = Main.GSON.fromJson(jsonResponse, JsonObject[].class);

        assertEquals(2, messages.length, "There should be 2 messages for the society");
        assertEquals("Some message here", messages[0].get("content").getAsString(), "First message content should match");
        assertEquals("2025-05-16T05:59:22Z", messages[0].get("timestamp").getAsString(), "First message timestamp should");
        assertEquals("Another message here", messages[1].get("content").getAsString(), "Second message content should match");
        assertEquals("2025-05-16T06:00:03Z", messages[1].get("timestamp").getAsString(), "Second message timestamp should");
    }

    @Test
    void testGetAllMessagesForEmptySociety() {
        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn("""
        {
            "id": 3
        }
        """);

        MessageManager.getAllMessages(ctx);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(ctx).result(captor.capture());
        String jsonResponse = captor.getValue();

        JsonObject[] messages = Main.GSON.fromJson(jsonResponse, JsonObject[].class);

        assertEquals(0, messages.length, "There should be 0 messages for a society with no messages");
    }

    @Test
    void testGetAllMessagesForNonexistentSociety() {
        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn("""
        {
            "id": 4
        }
        """);

        MessageManager.getAllMessages(ctx);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(ctx).result(captor.capture());
        String jsonResponse = captor.getValue();

        JsonObject[] messages = Main.GSON.fromJson(jsonResponse, JsonObject[].class);

        assertEquals(0, messages.length, "There should be 0 messages for a society that doesn't exist");
    }

    @Test
    void testSendMessage() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", token);
        jsonObject.addProperty("expiry", expiry.toString());
        jsonObject.addProperty("societyId", 2);
        jsonObject.addProperty("content", "Some message content");

        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn(jsonObject.toString());

        assertThrows(OkResponse.class, () -> MessageManager.sendMessage(ctx), "Successfully sending a message" +
                "should throw OkResponse");
    }

    @Test
    void testSendMessageToSocietyTheUserHasNotJoined() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", token);
        jsonObject.addProperty("expiry", expiry.toString());
        jsonObject.addProperty("societyId", 3);
        jsonObject.addProperty("content", "Some message content");

        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn(jsonObject.toString());

        assertThrows(UnauthorizedResponse.class, () -> MessageManager.sendMessage(ctx), "Attempting to send" +
                "a message to a society the user has not joined should throw UnauthorizedResponse");
    }

    @Test
    void testSendMessageToNonexistentSociety() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", token);
        jsonObject.addProperty("expiry", expiry.toString());
        jsonObject.addProperty("societyId", 4);
        jsonObject.addProperty("content", "Some message content");

        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn(jsonObject.toString());

        assertThrows(UnauthorizedResponse.class, () -> MessageManager.sendMessage(ctx), "Attempting to send" +
                "a message to a society the user has not joined should throw UnauthorizedResponse");
    }

    @Test
    void testSendMessageWithInvalidToken() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", "920a3c74-5228-4811-ac8a-43fb6b2070d6");
        jsonObject.addProperty("expiry", expiry.toString());
        jsonObject.addProperty("societyId", 2);
        jsonObject.addProperty("content", "Some message content");

        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn(jsonObject.toString());

        assertThrows(UnauthorizedResponse.class, () -> MessageManager.sendMessage(ctx), "Attempting to send" +
                "a message without a valid token/expiry combination should throw UnauthorizedResponse.");
    }

    @Test
    void testSendMessageWithIncorrectTokenExpiry() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", token);
        jsonObject.addProperty("expiry", Instant.now().toString());
        jsonObject.addProperty("societyId", 2);
        jsonObject.addProperty("content", "Some message content");

        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn(jsonObject.toString());

        assertThrows(UnauthorizedResponse.class, () -> MessageManager.sendMessage(ctx), "Attempting to send" +
                "a message without a valid token/expiry combination should throw UnauthorizedResponse.");
    }

}