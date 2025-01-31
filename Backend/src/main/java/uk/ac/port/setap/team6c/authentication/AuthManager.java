package uk.ac.port.setap.team6c.authentication;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;
import uk.ac.port.setap.team6c.Main;
import uk.ac.port.setap.team6c.database.User;

import java.time.Instant;
import java.util.UUID;

public class AuthManager {

    private static boolean verifyHash(@NotNull String string, String hashed) {
        return BCrypt.verifyer().verify(string.toCharArray(), hashed).verified;
    }

    public static void login(@NotNull Context ctx) {
        EmailPasswordRequest request = Main.GSON.fromJson(ctx.body(), EmailPasswordRequest.class);

        // Get the user from the database
        User user;
        try {
            user = new User(request.email());
        } catch (User.UnknownEmailException ignored) {
            throw new UnauthorizedResponse();
        }

        // Verify the password is correct
        if (!verifyHash(request.password(), user.getPassword()))
            throw new UnauthorizedResponse();

        // Generate the login token
        UUID token = UUID.randomUUID();
        Instant expiry = Instant.now().plusSeconds(60 * 60 * 24 * 30); // 30 days
        try {
            user.assignSessionToken(token, expiry);
        } catch (User.SessionTokenCouldNotBeCreatedException ignore) {
            token = null;
            expiry = null;
        }

        // Send token back to user
        ctx.result(Main.GSON.toJson(new LoginResponse(token, expiry, user.getUsername(), user.getEmail(), user.getProfilePicture(), user.isAdministrator(), user.getSettings())));
    }

    
}

