package uk.ac.port.setap.team6c.routes.authentication;

import java.time.Instant;

public record JoinEventRequest(int userid, int eventid, int societyid, Instant EndTimestamp) {
}
