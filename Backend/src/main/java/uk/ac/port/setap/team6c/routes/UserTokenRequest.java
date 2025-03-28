package uk.ac.port.setap.team6c.routes;

import java.time.Instant;

public record UserTokenRequest(String token, Instant expiry) {
}
