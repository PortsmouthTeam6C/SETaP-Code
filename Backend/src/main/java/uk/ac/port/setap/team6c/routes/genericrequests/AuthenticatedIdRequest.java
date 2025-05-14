package uk.ac.port.setap.team6c.routes.genericrequests;

import java.time.Instant;

/**
 * General request object for all requests requiring an authenticated user and some form of id. If an authenticated user
 * is not required, please prefer {@link IdRequest}
 * @param token The user's login token
 * @param expiry The token's expiry date
 * @param id The id
 */
public record AuthenticatedIdRequest(String token, Instant expiry, int id) {

}
