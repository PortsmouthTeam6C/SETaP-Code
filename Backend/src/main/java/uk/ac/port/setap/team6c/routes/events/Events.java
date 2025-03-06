package uk.ac.port.setap.team6c.routes.Events;

import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;
import uk.ac.port.setap.team6c.Main;
import uk.ac.port.setap.team6c.database.*;
import uk.ac.port.setap.team6c.routes.IdRequest;
import uk.ac.port.setap.team6c.routes.UserTokenRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Events {
    public static void getAllEvents(@NotNull Context ctx) {
        IdRequest request = Main.GSON.fromJson(ctx.body(), IdRequest.class);
        Society society;
        try {
            society = new Society(request.id());
        } catch (Society.UnknownSocietyException e) {
            ctx.status(400);
            ctx.result("Society not found");
            return;
        }
        society.getEvents();
    }
    public static void getJoinedEvents(@NotNull Context ctx) {
        UserTokenRequest request = Main.GSON.fromJson(ctx.body(), UserTokenRequest.class);
        User user;
        try {
            user = new User(UUID.fromString(request.token()), request.expiry());
        } catch (User.UnknownLoginTokenException e) {
            throw new UnauthorizedResponse();
        }
        EventCollection eventCollection = user.getJoinedEvents();
        List<EventResponse> events = convertEventCollectionToResponseList(eventCollection);
        ctx.result(Main.GSON.toJson(events));

    }
    private static @NotNull List<EventResponse> convertEventCollectionToResponseList(@NotNull EventCollection eventCollection) {
        List<EventResponse> events = new ArrayList<>();
        for (Event event : eventCollection) {
            events.add(new EventResponse(event.getEventId(), event.getUserid(), event.getStartTimestamp(), event.getEndTimestamp(), event.getCreationTimestamp(), event.getLocation(), event.getName(), event.getDescription()));
        }
        return events;
    }

}