package uk.ac.port.setap.team6c.routes.Events;

import java.time.Instant;

public record EventResponse(int eventid, int creatorid, Instant StartTimestamp, Instant EndTimeStamp, Instant CreationTimeStamp, String location, String name, String description) {
}
