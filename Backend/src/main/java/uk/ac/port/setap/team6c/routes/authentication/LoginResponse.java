package uk.ac.port.setap.team6c.routes.authentication;

import java.time.Instant;
import java.util.UUID;

/**
 * The response to a successful login request
 * @param token The login token the user can use to verify future requests
 * @param expiry The expiry date of the token
 * @param username The username of the user
 * @param email The email of the user
 * @param profilePicture The profile picture of the user
 * @param isAdministrator Whether the user is an administrator
 * @param settings JSON string of the user's settings
 */
public record LoginResponse(UUID token, Instant expiry, String username, String email, String profilePicture, boolean isAdministrator, String settings, String universityName, String universityTheming) {
}
