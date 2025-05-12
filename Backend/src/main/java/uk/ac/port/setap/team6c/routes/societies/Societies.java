package uk.ac.port.setap.team6c.routes.societies;

import io.javalin.http.ConflictResponse;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;
import uk.ac.port.setap.team6c.Main;
import uk.ac.port.setap.team6c.database.Society;
import uk.ac.port.setap.team6c.database.SocietyCollection;
import uk.ac.port.setap.team6c.database.University;
import uk.ac.port.setap.team6c.database.User;
import uk.ac.port.setap.team6c.routes.IdRequest;
import uk.ac.port.setap.team6c.routes.UserTokenRequest;
import uk.ac.port.setap.team6c.routes.authentication.LeaveSocietyRequest;
import uk.ac.port.setap.team6c.routes.authentication.LeaveSocietyResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Societies {

    /**
     * Get all societies in a university
     * @param ctx The Javalin context
     */
    public static void getAllSocieties(@NotNull Context ctx) {
        UniversityDomainRequest request = Main.GSON.fromJson(ctx.body(), UniversityDomainRequest.class);

        // Get university
        University university;
        try {
            university = new University(request.universityDomain());
        } catch (Exception e) {
            ctx.status(400);
            ctx.result("Invalid university domain");
            return;
        }

        // Get societies
        SocietyCollection societyCollection = university.getSocieties();
        List<SocietyResponse> societies = convertSocietyCollectionToResponseList(societyCollection);
        ctx.result(Main.GSON.toJson(societies));
    }

    /**
     * Get all societies that a user has joined
     * @param ctx The Javalin context
     */
    public static void getJoinedSocieties(@NotNull Context ctx) {
        UserTokenRequest request = Main.GSON.fromJson(ctx.body(), UserTokenRequest.class);

        // Get user
        User user;
        try {
            user = new User(UUID.fromString(request.token()), request.expiry());
        } catch (Exception e) {
            throw new UnauthorizedResponse();
        }

        // Get societies
        SocietyCollection societyCollection = user.getJoinedSocieties();
        List<SocietyResponse> societies = convertSocietyCollectionToResponseList(societyCollection);
        ctx.result(Main.GSON.toJson(societies));
    }

    /**
     * Get information about a society
     * @param ctx The Javalin context
     */
    public static void getSocietyInfo(@NotNull Context ctx) {
        IdRequest request = Main.GSON.fromJson(ctx.body(), IdRequest.class);

        // Get society
        Society society;
        try {
            society = new Society(request.id());
        } catch (Exception e) {
            ctx.status(400);
            ctx.result("Invalid society ID");
            return;
        }

        ctx.result(Main.GSON.toJson(new SocietyResponse(society.getSocietyId(), society.getSocietyName(),
                society.getSocietyDescription(), society.getSocietyPicture(), society.getMaxSize(), society.isPaid())));
    }

    /**
     * Convert a society collection to a list of society responses
     * @param societyCollection The society collection
     * @return A list of society responses
     */
    private static @NotNull List<SocietyResponse> convertSocietyCollectionToResponseList(@NotNull SocietyCollection societyCollection) {
        List<SocietyResponse> societies = new ArrayList<>();
        for (Society society : societyCollection) {
            societies.add(new SocietyResponse(society.getSocietyId(), society.getSocietyName(), society.getSocietyDescription(),
                    society.getSocietyPicture(), society.getMaxSize(), society.isPaid()));
        }
        return societies;
    }

    public static void joinSociety(@NotNull Context ctx) {
        //TBD
    }

    public static void leaveSociety(@NotNull Context ctx) {
        LeaveSocietyRequest request = Main.GSON.fromJson(ctx.body(), LeaveSocietyRequest.class);
        User user;
        try{
            user = new User(request.userid());
        } catch (Exception ignored) {
            throw new ConflictResponse();
        }
        Society society;
        try{
            society = new Society(request.societyid());
        } catch (Exception ignored) {
            throw new ConflictResponse();
        }
        if (!user.getJoinedSocieties().contains(society)){
            throw new UnauthorizedResponse();
        }
        ctx.result(Main.GSON.toJson(new LeaveSocietyResponse(request.userid(), request.societyid())));
    }

}
