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

class SocietyManagerTest {

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
    ('University of Portsmouth', 'https://upload.wikimedia.org/wikipedia/commons/d/dc/University_of_Portsmouth_Logo.png', '@port.ac.uk', '{"primarycolor":"#572985"}'),
    ('University of Southampton', 'https://upload.wikimedia.org/wikipedia/en/thumb/3/32/University_of_Southampton_Logo.png/1200px-University_of_Southampton_Logo.png', '@soton.ac.uk', '{"primarycolor":"#005c85"}');
    
    insert into users (universityId, username, email, password, profilePicture) values
    (1, 'johndoe', 'johndoe@port.ac.uk', '$2y$10$wr1OF4PvzJX0nrfsJ6mumuriuI5MzNPdF.9nxzzElz2mldImt2n.O', 'https://api.dicebear.com/9.x/shapes/svg?seed=johndoe');
    
    insert into society (universityid, societyname, societydescription, societypicture) values
    (1, 'Test Society 1', 'Some description here 1', 'https://www.example.com/image1.jpg'),
    (1, 'Test Society 2', 'Some description here', 'https://www.example.com/image.jpg'),
    (1, 'Test Society 3', 'Some description here', 'https://www.example.com/image.jpg');
    
    insert into societymember (userid, societyid) values (1, 1), (1, 2);
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
    void getAllSocieties() {
        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn("""
        {
            "id": 1
        }
        """);

        SocietyManager.getAllSocieties(ctx);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(ctx).result(captor.capture());
        String jsonResponse = captor.getValue();

        JsonObject[] societies = Main.GSON.fromJson(jsonResponse, JsonObject[].class);
        assertEquals(3, societies.length, "There should be 3 societies at the university.");
        assertEquals("Test Society 1", societies[0].get("societyName").getAsString(),
                "The first society should be called Test Society 1");
        assertEquals("Some description here 1", societies[0].get("societyDescription").getAsString(),
                "The first society's description should be Some description here 1");
        assertEquals("https://www.example.com/image1.jpg", societies[0].get("societyPicture").getAsString(),
                "The first society's image should be https://www.example.com/image1.jpg");
        assertEquals("Test Society 2", societies[1].get("societyName").getAsString(),
                "The second society should be called Test Society 2");
        assertEquals("Test Society 3", societies[2].get("societyName").getAsString(),
                "The third society should be called Test Society 3");
    }

    @Test
    void testGetAllSocietiesForEmptyUniversity() {
        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn("""
        {
            "id": 2
        }
        """);

        SocietyManager.getAllSocieties(ctx);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(ctx).result(captor.capture());
        String jsonResponse = captor.getValue();

        JsonObject[] societies = Main.GSON.fromJson(jsonResponse, JsonObject[].class);
        assertEquals(0, societies.length, "There should be 0 societies at the university.");
    }

    @Test
    void testGetAllSocietiesForNonexistentUniversity() {
        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn("""
        {
            "id": 3
        }
        """);

        SocietyManager.getAllSocieties(ctx);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(ctx).result(captor.capture());
        String jsonResponse = captor.getValue();

        JsonObject[] societies = Main.GSON.fromJson(jsonResponse, JsonObject[].class);
        assertEquals(0, societies.length, "There should be 0 societies at the university.");
    }

    @Test
    void testGetAllJoinedSocieties() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", token);
        jsonObject.addProperty("expiry", expiry.toString());

        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn(jsonObject.toString());

        SocietyManager.getAllJoinedSocieties(ctx);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(ctx).result(captor.capture());
        String jsonResponse = captor.getValue();

        JsonObject[] societies = Main.GSON.fromJson(jsonResponse, JsonObject[].class);
        assertEquals(2, societies.length, "The user should have joined 2 societies.");
    }

    @Test
    void testGetAllJoinedSocietiesWithInvalidToken() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", "920a3c74-5228-4811-ac8a-43fb6b2070d6");
        jsonObject.addProperty("expiry", expiry.toString());

        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn(jsonObject.toString());

        assertThrows(UnauthorizedResponse.class, () -> SocietyManager.getAllJoinedSocieties(ctx), "Attempting" +
                " to send a message without a valid token/expiry combination should throw UnauthorizedResponse.");
    }

    @Test
    void testGetAllJoinedSocietiesWithIncorrectTokenExpiry() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", token);
        jsonObject.addProperty("expiry", Instant.now().toString());

        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn(jsonObject.toString());

        assertThrows(UnauthorizedResponse.class, () -> SocietyManager.getAllJoinedSocieties(ctx), "Attempting" +
                " to send a message without a valid token/expiry combination should throw UnauthorizedResponse.");
    }

    @Test
    void testJoinSociety() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", token);
        jsonObject.addProperty("expiry", expiry.toString());
        jsonObject.addProperty("id", 1);

        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn(jsonObject.toString());

        assertThrows(OkResponse.class, () -> SocietyManager.joinSociety(ctx), "Successfully joining a society" +
                "should throw OkResponse.");
    }

    @Test
    void testJoinSocietyWithInvalidToken() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", "920a3c74-5228-4811-ac8a-43fb6b2070d6");
        jsonObject.addProperty("expiry", expiry.toString());
        jsonObject.addProperty("id", 1);

        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn(jsonObject.toString());

        assertThrows(UnauthorizedResponse.class, () -> SocietyManager.joinSociety(ctx), "Attempting to send" +
                "a message without a valid token/expiry combination should throw UnauthorizedResponse.");
    }

    @Test
    void testJoinSocietyWithIncorrectTokenExpiry() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", token);
        jsonObject.addProperty("expiry", Instant.now().toString());
        jsonObject.addProperty("id", 1);

        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn(jsonObject.toString());

        assertThrows(UnauthorizedResponse.class, () -> SocietyManager.joinSociety(ctx), "Attempting to send" +
                "a message without a valid token/expiry combination should throw UnauthorizedResponse.");
    }

}
