package uk.ac.port.setap.team6c.routes.messages;

import uk.ac.port.setap.team6c.routes.UserResponse;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record MessagesResponse(List<MessageResponse> messages, Map<String, UserResponse> users) {
}
