package uk.ac.port.setap.team6c.routes.messages;

import java.time.Instant;

public record MessageRequest(
        // Authenticate the user
        String token,
        Instant expiry,

        // Message filters
        String messageContent,
        String senderEmail,
        Integer societyId,
        Instant after,
        Instant before
) {}
