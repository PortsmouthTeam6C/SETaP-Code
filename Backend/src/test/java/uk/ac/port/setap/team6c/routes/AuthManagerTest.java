package uk.ac.port.setap.team6c.routes;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.cdimascio.dotenv.DotenvEntry;
import io.javalin.http.Context;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.http.UnprocessableContentResponse;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import uk.ac.port.setap.team6c.Main;
import uk.ac.port.setap.team6c.database.DatabaseManager;
import uk.ac.port.setap.team6c.database.User;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthManagerTest {

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

    insert into sessiontoken (token, userid, expiry) values
    ('f47ac10b-58cc-4372-a567-0e02b2c3d479', 1, '2025-06-13T12:24:01.849390400Z');
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
    void testHashedPasswordNotNull() {
        String plaintext = "password";
        String hashed = AuthManager.hashPassword(plaintext);
        assertNotNull(hashed, "Hashed password should not be null.");
    }

    @Test
    void testHashedPasswordNotPlaintext() {
        String plaintext = "password";
        String hashed = AuthManager.hashPassword(plaintext);
        assertNotEquals(plaintext, hashed, "Hashed password should not be the same as the plaintext password.");
    }

    @Test
    void testVerifyHashedPassword() {
        String plaintext = "password";
        String hashed = AuthManager.hashPassword(plaintext);
        boolean verified = AuthManager.verifyHash(plaintext, hashed);

        assertTrue(verified, "Hashed password should match the plaintext password when verified.");
    }

    @Test
    void testCorrectLogin() {
        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn("""
            {
                "email": "johndoe@port.ac.uk",
                "password": "password"
            }
        """);
        AuthManager.login(ctx);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(ctx).result(captor.capture());
        JsonObject jsonObject = JsonParser.parseString(captor.getValue()).getAsJsonObject();

        assertDoesNotThrow(() -> UUID.fromString(jsonObject.get("token").getAsString()), "Token should be a valid UUID");
        assertDoesNotThrow(() -> Instant.parse(jsonObject.get("expiry").getAsString()), "Expiry should be a valid date");
        assertNotNull(jsonObject.get("username").getAsString(), "Username should not be null");
        assertNotNull(jsonObject.get("email").getAsString(), "Email should not be null");
        assertNotNull(jsonObject.get("profilePicture").getAsString(), "Profile picture should not be null");
        assertTrue(jsonObject.get("universityId").getAsInt() > 0, "University ID should be a positive integer");
        assertNotNull(jsonObject.get("universityPicture").getAsString(), "University picture should not be null");
        assertNotNull(jsonObject.get("theming").getAsString(), "Theming should not be null");
    }

    @Test
    void testUnknownEmailLogin() {
        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn("""
            {
                "email": "james@port.ac.uk",
                "password": "password"
            }
        """);

        assertThrows(UnauthorizedResponse.class, () -> AuthManager.login(ctx), "Should throw " +
                "UnauthorizedResponse if the user tries to log in with an unknown email");
    }

    @Test
    void testIncorrectPasswordLogin() {
        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn("""
            {
                "email": "johndoe@port.ac.uk",
                "password": "12345"
            }
        """);

        assertThrows(UnauthorizedResponse.class, () -> AuthManager.login(ctx), "Should throw " +
                "UnauthorizedResponse if the user tries to log in with an unknown email");
    }

    @Test
    void testSignup() {
        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn("""
        {
            "email": "janedoe@port.ac.uk",
            "username": "janedoe",
            "password": "password123"
        }
    """);

        AuthManager.signup(ctx);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(ctx).result(captor.capture());
        JsonObject jsonObject = JsonParser.parseString(captor.getValue()).getAsJsonObject();

        assertDoesNotThrow(() -> UUID.fromString(jsonObject.get("token").getAsString()), "Token should be a valid UUID");
        assertDoesNotThrow(() -> Instant.parse(jsonObject.get("expiry").getAsString()), "Expiry should be a valid date");
        assertEquals("janedoe", jsonObject.get("username").getAsString(), "Username should match the request");
        assertEquals("janedoe@port.ac.uk", jsonObject.get("email").getAsString(), "Email should match the request");
        assertNotNull(jsonObject.get("profilePicture").getAsString(), "Profile picture should not be null");
        assertTrue(jsonObject.get("universityId").getAsInt() > 0, "University ID should be a positive integer");
        assertNotNull(jsonObject.get("universityPicture").getAsString(), "University picture should not be null");
        assertNotNull(jsonObject.get("theming").getAsString(), "Theming should not be null");

        // Verify the user was created in the database
        User user = User.get("janedoe@port.ac.uk");
        assertNotNull(user, "User should be created in the database");
        assertEquals("janedoe", user.getUsername(), "Username should match the created user");
        assertEquals("janedoe@port.ac.uk", user.getEmail(), "Email should match the created user");
    }

    @Test
    void testUnknownUniversitySignup() {
        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn("""
        {
            "email": "janedoe@soton.ac.uk",
            "username": "janedoe",
            "password": "password123"
        }
    """);

        assertThrows(UnprocessableContentResponse.class, () -> AuthManager.signup(ctx), "Should throw " +
                "UnprocessableContentResponse if the user tries to sign up with an unknown email");
    }

    @Test
    void testInvalidUniversitySignup() {
        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn("""
        {
            "email": "janedoe.ac.uk",
            "username": "janedoe",
            "password": "password123"
        }
    """);

        assertThrows(UnprocessableContentResponse.class, () -> AuthManager.signup(ctx), "Should throw " +
                "UnprocessableContentResponse if the user tries to sign up with an unknown email");
    }

    @Test
    void testDuplicateEmailSignup() {
        Context ctx = Mockito.mock(Context.class);
        when(ctx.body()).thenReturn("""
        {
            "email": "johndoe@port.ac.uk",
            "username": "johndoe",
            "password": "password123"
        }
    """);

        assertThrows(InternalServerErrorResponse.class, () -> AuthManager.signup(ctx), "Should throw " +
                "InternalServerErrorResponse if the user tries to sign up with an unknown email");
    }

}
