package uk.ac.port.setap.team6c.routes.events;

import io.javalin.http.ConflictResponse;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;
import uk.ac.port.setap.team6c.Main;
import uk.ac.port.setap.team6c.database.*;
import uk.ac.port.setap.team6c.routes.IdRequest;
import uk.ac.port.setap.team6c.routes.UserTokenRequest;
import uk.ac.port.setap.team6c.routes.authentication.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Events {

    public static void getAllEvents(@NotNull Context ctx) {
        IdRequest request = Main.GSON.fromJson(ctx.body(), IdRequest.class);
        Society society;
        try {
            society = new Society(request.id());
        } catch (Exception e) {
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
        } catch (Exception e) {
            throw new UnauthorizedResponse();
        }
        EventCollection eventCollection = user.getJoinedEvents();
        List<EventResponse> events = convertEventCollectionToResponseList(eventCollection);
        ctx.result(Main.GSON.toJson(events));

    }

    public static void getEventInfo(@NotNull Context ctx) {
        IdRequest request = Main.GSON.fromJson(ctx.body(), IdRequest.class);
        Event event;
        try{
            event = new Event(request.id());

        } catch (Exception e){
            ctx.status(400);
            ctx.result("Eventid not found");
            return;
        }
        ctx.result(Main.GSON.toJson(new EventResponse(event.getEventId(),event.getUserid(),event.getStartTimestamp(),event.getEndTimestamp(),event.getCreationTimestamp(),event.getLocation(),event.getName(),event.getDescription())));
    }

    private static @NotNull List<EventResponse> convertEventCollectionToResponseList(@NotNull EventCollection eventCollection) {
        List<EventResponse> events = new ArrayList<>();
        for (Event event : eventCollection) {
            events.add(new EventResponse(event.getEventId(), event.getUserid(), event.getStartTimestamp(), event.getEndTimestamp(), event.getCreationTimestamp(), event.getLocation(), event.getName(), event.getDescription()));
        }
        return events;
    }

    public static void createEvent(@NotNull Context ctx) {
        CreateEventRequest request = Main.GSON.fromJson(ctx.body(), CreateEventRequest.class);

        // Check user info
        User user;
        try{
            user = new User(request.userid());
        } catch (Exception ignored) {
            throw new ConflictResponse();
        }
        // Check society info
        Society society;
        try{
            society = new Society(request.societyid());
        } catch (Exception ignored) {
            throw new ConflictResponse();
        }

        // check user is an administrator, if not check if they are a manager
        if(!user.isAdministrator()){
            try{
                if (!society.getManagers().contains(user)){
                    throw new UnauthorizedResponse();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // Check start and end timestamps
        if (request.StartTimestamp().isAfter(request.EndTimeStamp())) {
            throw new ConflictResponse();
        }
        if (request.StartTimestamp().isBefore(request.CreationTimestamp())) {
            throw new ConflictResponse();
        }

        // Response
        ctx.result(Main.GSON.toJson(new CreateEventResponse(request.userid(), request.StartTimestamp(),
                request.EndTimeStamp(), request.CreationTimestamp(), request.location(), request.name(),
                request.description())));

    }

    public static void joinEvent(@NotNull Context ctx) {
        JoinEventRequest request = Main.GSON.fromJson(ctx.body(), JoinEventRequest.class);

        //get society and user info
        Society society;
        try{
            society = new Society(request.societyid());
        } catch (Exception ignored) {
            throw new ConflictResponse();
        }

        User user;
        try{
            user = new User(request.userid());
        } catch (Exception ignored) {
            throw new ConflictResponse();
        }

        //Check user is in society
        if (!user.getJoinedSocieties().contains(society)){
            throw new UnauthorizedResponse();
        }

        //check event has not already ended
        if (request.EndTimestamp().isBefore(Instant.now())) {
            throw new ConflictResponse();
        }

        ctx.result(Main.GSON.toJson(new JoinEventResponse(request.userid(), request.eventid())));
    }

    public static void leaveEvent(@NotNull Context ctx) {
        LeaveEventRequest request = Main.GSON.fromJson(ctx.body(), LeaveEventRequest.class);

        User user;
        try{
            user = new User(request.userid());
        } catch (Exception ignored) {
            throw new ConflictResponse();
        }

        Event event;
        try{
            event = new Event(request.eventid());
        } catch (Exception ignored) {
            throw new ConflictResponse();
        }

        if (!user.getJoinedEvents().contains(event)){
            throw new UnauthorizedResponse();
        }
        if (request.EndTimestamp().isBefore(Instant.now())) {
            throw new ConflictResponse();
        }

        ctx.result(Main.GSON.toJson(new LeaveEventResponse(request.userid(), request.eventid())));
    }

    public static void deleteEvent(@NotNull Context ctx) {

    }

}