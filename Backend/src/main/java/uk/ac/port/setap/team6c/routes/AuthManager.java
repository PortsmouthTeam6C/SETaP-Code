package uk.ac.port.setap.team6c.routes;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.javalin.http.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.ac.port.setap.team6c.Main;
import uk.ac.port.setap.team6c.database.University;
import uk.ac.port.setap.team6c.database.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

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
     * Route to log the user in with a provided email and password and get a login token
     * @param ctx The request context
     */
    public static void login(@NotNull Context ctx) {
        EmailPasswordRequest request = Main.GSON.fromJson(ctx.body(), EmailPasswordRequest.class);

        // Get the user, if none are found return 401 Unauthorized
        User user = User.get(request.email);
        if (user == null)
            throw new UnauthorizedResponse();

        // Check the user's password, if it is wrong return 401 Unauthorized
        if(!verifyHash(request.password, user.getPassword()))
            throw new UnauthorizedResponse();

        // Assign login tokens, default to null if creation fails
        String token = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plus(30, ChronoUnit.DAYS);
        if (!user.assignSessionToken(token, expiry)) {
            token = null;
            expiry = null;
        }

        // Get the user's university
        University university = University.get(user.getUniversityId());
        if (university == null)
            throw new GoneResponse();

        // Return all necessary information to the user
        ctx.result(Main.GSON.toJson(new LoginResponse(token, expiry, user.getUsername(), user.getEmail(),
                user.getProfilePicture(), university.getUniversityId(), university.getUniversityPicture(),
                university.getTheming())));
    }

    /**
     * Route to log the user in with a provided login token
     * @param ctx The request context
     */
    public static void loginWithToken(@NotNull Context ctx) {
        TokenExpiryRequest request = Main.GSON.fromJson(ctx.body(), TokenExpiryRequest.class);

        User user = User.get(request.token, request.expiry);
        if (user == null)
            throw new UnauthorizedResponse();

        University university = University.get(user.getUniversityId());
        if (university == null)
            throw new GoneResponse();

        ctx.result(Main.GSON.toJson(new LoginResponse(request.token, request.expiry, user.getUsername(),
                user.getEmail(), user.getProfilePicture(), university.getUniversityId(),
                university.getUniversityPicture(), university.getTheming())));
    }

    /**
     * Route to create a new user account
     * @param ctx The request context
     */
    public static void signup(@NotNull Context ctx) {
        SignupRequest request = Main.GSON.fromJson(ctx.body(), SignupRequest.class);

        String hashedPassword = hashPassword(request.password);
        String profilePicture = "https://api.dicebear.com/9.x/shapes/svg?seed=" + request.username;

        University university = University.get('@' + request.email.split("@")[1]);
        if (university == null)
            throw new UnprocessableContentResponse();

        User user = User.create(university.getUniversityId(), request.username, request.email, hashedPassword,
                profilePicture);
        if (user == null)
            throw new InternalServerErrorResponse();

        // Assign login tokens, default to null if creation fails
        String token = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plus(30, ChronoUnit.DAYS);
        if (!user.assignSessionToken(token, expiry)) {
            token = null;
            expiry = null;
        }

        ctx.result(Main.GSON.toJson(new LoginResponse(token, expiry, request.username, request.email,
                profilePicture, university.getUniversityId(), university.getUniversityPicture(),
                university.getTheming())));
    }

    private record EmailPasswordRequest(String email, String password) {}
    public record TokenExpiryRequest(String token, Instant expiry) {}
    private record SignupRequest(String email, String username, String password) {}

    private record LoginResponse(String token, Instant expiry, String username, String email, String profilePicture,
                                 int universityId, String universityPicture, String theming) {}

}
