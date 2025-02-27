package uk.ac.port.setap.team6c.routes.Events;

import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import uk.ac.port.setap.team6c.Main;
import uk.ac.port.setap.team6c.database.Event;
import uk.ac.port.setap.team6c.database.Society;
import uk.ac.port.setap.team6c.database.SocietyCollection;
import uk.ac.port.setap.team6c.routes.IdRequest;

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

}