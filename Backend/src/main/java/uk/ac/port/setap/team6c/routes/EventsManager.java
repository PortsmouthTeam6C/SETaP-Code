package uk.ac.port.setap.team6c.routes;

import io.javalin.http.Context;
import io.javalin.http.OkResponse;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;
import uk.ac.port.setap.team6c.Main;
import uk.ac.port.setap.team6c.database.Event;
import uk.ac.port.setap.team6c.database.EventCollection;
import uk.ac.port.setap.team6c.database.User;
import uk.ac.port.setap.team6c.routes.genericrequests.IdRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class EventsManager {

    /**
     * Route to get all events for a specific society
     * @param ctx The request context
     */
    public static void getAllEvents(@NotNull Context ctx) {
        IdRequest request = Main.GSON.fromJson(ctx.body(), IdRequest.class);

        EventCollection eventCollection = Event.getAllEvents(request.id());
        List<EventResponse> events = eventCollectionToResponseList(eventCollection);
        ctx.result(Main.GSON.toJson(events));
    }

    /**
     * Route to create a new event
     * @param ctx The request context
     */
    public static void createEvent(@NotNull Context ctx) {
        CreateEventRequest request = Main.GSON.fromJson(ctx.body(), CreateEventRequest.class);

        // If the user doesn't exist, they're not authorized to create an event
        User user = User.get(request.token, request.expiry);
        if (user == null)
            throw new UnauthorizedResponse();

        // If the user is not a member of the society, they can't create an event in it
        if (!user.hasJoinedSociety(request.societyId))
            throw new UnauthorizedResponse();

        Event.create(request.societyId, request.date, request.location, request.name, request.description,
                request.price, request.image);
        throw new OkResponse();
    }

    /**
     * Converts an EventCollection into a list of EventResponse objects, which can be sent to the user
     * @param eventCollection The collection of events to convert
     * @return The list of EventResponse objects
     */
    private static @NotNull List<EventResponse> eventCollectionToResponseList(@NotNull EventCollection eventCollection) {
        List<EventResponse> events = new ArrayList<>();
        for (Event event : eventCollection) {
            events.add(new EventResponse(event.getEventId(), event.getDate(), event.getLocation(), event.getName(),
                    event.getDescription(), event.getPrice(), event.getImage()));
        }
        return events;
    }

    private record CreateEventRequest(int societyId, String token, Instant expiry, Instant date, String location,
                                      String name, String description, int price, String image) {}

    private record EventResponse(int eventId, Instant date, String location, String name, String description, int price,
                                 String image) {}

}
