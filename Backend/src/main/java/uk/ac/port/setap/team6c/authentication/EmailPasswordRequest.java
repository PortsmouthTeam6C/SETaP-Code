package uk.ac.port.setap.team6c.authentication;

/**
 * POST request body for logging in with an email and password
 * @param email The email to log in with
 * @param password The plaintext password to log in with
 */
public record EmailPasswordRequest(String email, String password) {
}
