package uk.ac.port.setap.team6c.routes.messages;

import java.time.Instant;

record MessageResponse(int id, String content, Instant timestamp, boolean isPinned, int senderId) {
}
