package uk.ac.port.setap.team6c.routes.authentication;

import java.time.Instant;

public record CreateEventResponse(int userid, Instant StartTimestamp, Instant EndTimeStamp, Instant CreationTimestamp, String location, String name, String description) {
}
