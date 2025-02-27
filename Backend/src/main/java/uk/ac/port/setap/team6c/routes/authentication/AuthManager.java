package uk.ac.port.setap.team6c.routes.authentication;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.javalin.http.Context;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.ac.port.setap.team6c.Main;
import uk.ac.port.setap.team6c.database.University;
import uk.ac.port.setap.team6c.database.User;

import java.time.Instant;
import java.util.UUID;

/**
 * Manages all authentication for the application
 */
public class AuthManager {

    /**
     * Verifies that the provided string matches the hashed string
     * @param password The plaintext password to check
     * @param hashed The hashed password to check against
     * @return Whether the password matches the hash
     */
    private static boolean verifyHash(@NotNull String password, String hashed) {
        return BCrypt.verifyer().verify(password.toCharArray(), hashed).verified;
    }

    /**
     * Hashes a password using BCrypt
     * @param password The password to hash
     * @return The hashed password
     */
    @Contract("_ -> new")
    public static @NotNull String hashPassword(@NotNull String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    /**
     * Route to log the router in with a provided email and password and get a login token
     * @param ctx The request context
     */
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

        // Get the university
        University university;
        try {
            university = user.getUniversity();
        } catch (University.UniversityNotFoundException e) {
            throw new InternalServerErrorResponse();
        }

        // Send token back to user
        ctx.result(Main.GSON.toJson(new LoginResponse(
                token, expiry, user.getUsername(), user.getEmail(), user.getProfilePicture(),
                user.isAdministrator(), user.getSettings(), university.getUniversityName(), university.getTheming())));
    }
    
}
