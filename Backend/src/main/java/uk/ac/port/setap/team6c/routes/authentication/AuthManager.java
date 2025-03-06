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
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Manages all authentication for the application
 */
public class AuthManager {

    private static Random RANDOM = new Random();
    /**
     * A map of users waiting for verification
     * Key: UUID + Verification code concatenated
     * Value: All the user-supplied information about the account
     */
    private static Map<String, CreateAccountRequest> usersWaitingForVerification;

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

    /**
     * Create a new account & send a verification email
     * @param ctx The request context
     */
    public static void createAccount(@NotNull Context ctx) {
        CreateAccountRequest request = Main.GSON.fromJson(ctx.body(), CreateAccountRequest.class);

        /* Todo: ensure email is not currently in use in the database or present in {@link usersWaitingForVerification} */

        String accountIdentifier = UUID.randomUUID().toString();
        String verificationCode = padWithZeroes(String.valueOf(RANDOM.nextInt(1000000)));
        usersWaitingForVerification.put(accountIdentifier + verificationCode, request);

        /* Todo: remove the user from {@link usersWaitingForVerification} after a certain amount of time */

        // Send verification email
        /* Todo: Implement email sending
            - Attach to GMail API
            - Log in with OAuth2
            - Create email draft with verification code
            - Send email
         */

        ctx.result(Main.GSON.toJson(new CreateAccountResponse(accountIdentifier)));
    }

    /**
     * Add zeroes to the left of a string until the full length of the string is 6 characters
     * @param string The string to pad
     * @return The padded string
     */
    private static @NotNull String padWithZeroes(@NotNull String string) {
        return "0".repeat(6 - string.length()) + string;
    }

}
