package uk.ac.port.setap.team6c.routes.authentication;

import java.time.Instant;

public record LeaveEventRequest(int userid, int eventid, Instant EndTimestamp) {
}
