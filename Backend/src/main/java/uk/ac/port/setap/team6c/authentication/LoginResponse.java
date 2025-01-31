package uk.ac.port.setap.team6c.authentication;

import java.time.Instant;
import java.util.UUID;

public record LoginResponse(UUID token, Instant expiry, String username, String email, String profilePicture, boolean isAdministrator, String settings) {
}
