package uk.ac.port.setap.team6c.routes.genericrequests;

/**
 * General request object for all requests some form of id. If an authenticated user is required, please prefer
 * {@link AuthenticatedIdRequest}
 * @param id The id
 */
public record IdRequest(int id) {

}
