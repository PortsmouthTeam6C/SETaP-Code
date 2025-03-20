package uk.ac.port.setap.team6c.routes.authentication;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.javalin.http.ConflictResponse;
import io.javalin.http.Context;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.ac.port.setap.team6c.Main;
import uk.ac.port.setap.team6c.database.Society;
import uk.ac.port.setap.team6c.database.University;
import uk.ac.port.setap.team6c.database.User;
import uk.ac.port.setap.team6c.email.GmailManager;
import uk.ac.port.setap.team6c.routes.Events.EventResponse;

import java.time.Instant;
import java.util.*;

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
    private static Map<String, CreateAccountRequest> usersWaitingForVerification = new HashMap<>();

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
     * @throws ConflictResponse If the email is in use
     */
    public static void createAccount(@NotNull Context ctx) {
        CreateAccountRequest request = Main.GSON.fromJson(ctx.body(), CreateAccountRequest.class);

        // If the user is instantiated without error, the user already exists in the database
        if (User.exists(request.email())) {
            throw new ConflictResponse();
        }

        // If the user already exists in the list of those waiting for verification, remove the older one
        for (String key : usersWaitingForVerification.keySet())
            if (usersWaitingForVerification.get(key).email().equals(request.email()))
                usersWaitingForVerification.remove(key);

        // Add this user to the list of users waiting for verification
        String accountIdentifier = UUID.randomUUID().toString();
        String verificationCode = padWithZeroes(String.valueOf(RANDOM.nextInt(1000000)));
        usersWaitingForVerification.put(accountIdentifier + verificationCode, request);

        // Stop waiting for verification after 10 minutes
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                usersWaitingForVerification.remove(accountIdentifier + verificationCode);
            }
        }, 10 * 60 * 1000);

        // Send verification email
        GmailManager.sendEmail(request.email(), "Verification code", "Your verification code is: " + verificationCode);

        // Respond with the account identifier
        ctx.result(Main.GSON.toJson(new CreateAccountResponse(accountIdentifier)));
    }

    public static void verifyAccount(@NotNull Context ctx) {

    }

    /**
     * Add zeroes to the left of a string until the full length of the string is 6 characters
     * @param string The string to pad
     * @return The padded string
     */
    private static @NotNull String padWithZeroes(@NotNull String string) {
        return "0".repeat(6 - string.length()) + string;
    }

    public static void createEvent(@NotNull Context ctx) {
        CreateEventRequest request = Main.GSON.fromJson(ctx.body(), CreateEventRequest.class);

        // Check user info
        User user;
        try{
            user = new User(request.userid());
        } catch (User.UnknownUseridException ignored) {
            throw new ConflictResponse();
        }
        // Check society info
        Society society;
        try{
            society = new Society(request.societyid());
        } catch (Society.UnknownSocietyException ignored) {
            throw new ConflictResponse();
        }
        // check user is an administrator, if not check if they are a manager
        if(!user.isAdministrator()){
            try{
                if (!society.getManagers().contains(user)){
                    throw new UnauthorizedResponse();
                }
            } catch (Society.UnknownSocietyException e) {
                throw new RuntimeException(e);
            }
        }

        // Check start and end timestamps
        if (request.StartTimestamp().isAfter(request.EndTimeStamp())) {
            throw new ConflictResponse();
        }
        if (request.StartTimestamp().isBefore(request.CreationTimestamp())) {
            throw new ConflictResponse();
        }
        // Response
        ctx.result(Main.GSON.toJson(new CreateEventResponse(request.userid(),request.StartTimestamp(),request.EndTimeStamp(), request.CreationTimestamp(),request.location(),request.name(),request.description())));

    }

}
