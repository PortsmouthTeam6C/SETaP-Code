package uk.ac.port.setap.team6c.routes;

import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;
import uk.ac.port.setap.team6c.Main;
import uk.ac.port.setap.team6c.database.Society;
import uk.ac.port.setap.team6c.database.SocietyCollection;
import uk.ac.port.setap.team6c.database.User;
import uk.ac.port.setap.team6c.routes.genericrequests.AuthenticatedIdRequest;
import uk.ac.port.setap.team6c.routes.genericrequests.IdRequest;

import java.util.ArrayList;
import java.util.List;

public class SocietyManager {

    /**
     * Route to get all societies for a specific university.
     * @param ctx The request context
     */
    public static void getAllSocieties(@NotNull Context ctx) {
        IdRequest request = Main.GSON.fromJson(ctx.body(), IdRequest.class);

        SocietyCollection societyCollection = Society.getAllSocieties(request.id());
        List<SocietyResponse> societies = societyCollectionToResponseList(societyCollection);
        ctx.result(Main.GSON.toJson(societies));
    }

    /**
     * Route to get all societies a specific user has joined
     * @param ctx The request context
     */
    public static void getAllJoinedSocieties(@NotNull Context ctx) {
        AuthenticatedIdRequest request = Main.GSON.fromJson(ctx.body(), AuthenticatedIdRequest.class);

        User user = User.get(request.token(), request.expiry());
        if (user == null)
            throw new UnauthorizedResponse();

        SocietyCollection societyCollection = Society.getAllSocieties(request.id()).filter(user::hasJoinedSociety);
        List<SocietyResponse> societies = societyCollectionToResponseList(societyCollection);
        ctx.result(Main.GSON.toJson(societies));
    }

    /**
     * Route to join a society
     * @param ctx The request context
     */
    public static void joinSociety(@NotNull Context ctx) {
        AuthenticatedIdRequest request = Main.GSON.fromJson(ctx.body(), AuthenticatedIdRequest.class);

        User user = User.get(request.token(), request.expiry());
        if (user == null)
            throw new UnauthorizedResponse();

        Society.join(user.getUserId(), request.id());
    }

    /**
     * Converts a SocietyCollection into a list of SocietyResponse objects, which can be sent to the user
     * @param societyCollection The collection of societies to convert
     * @return The list of SocietyResponse objects
     */
    private static @NotNull List<SocietyResponse> societyCollectionToResponseList(@NotNull SocietyCollection societyCollection) {
        List<SocietyResponse> societies = new ArrayList<>();
        for (Society society : societyCollection) {
            societies.add(new SocietyResponse(society.getSocietyId(), society.getUniversityId(),
                    society.getSocietyName(), society.getDescription(), society.getSocietyPicture()));
        }
        return societies;
    }

    private record SocietyResponse(int societyId, int universityId, String societyName, String societyDescription,
                                   String societyPicture) {}

}
