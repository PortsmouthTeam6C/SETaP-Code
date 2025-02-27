package uk.ac.port.setap.team6c.societies;

import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import uk.ac.port.setap.team6c.Main;
import uk.ac.port.setap.team6c.database.Society;
import uk.ac.port.setap.team6c.database.SocietyCollection;
import uk.ac.port.setap.team6c.database.University;

import java.util.ArrayList;
import java.util.List;

public class Societies {

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
        List<SocietyResponse> societies = new ArrayList<>();
        for (Society society : societyCollection) {
            societies.add(new SocietyResponse(society.getSocietyId(), society.getSocietyName(), society.getSocietyDescription(),
                    society.getSocietyPicture(), society.getMaxSize(), society.isPaid()));
        }
        ctx.result(Main.GSON.toJson(societies));
    }

    public static void getSocietyInfo(@NotNull Context ctx) {
        SocietyIdRequest request = Main.GSON.fromJson(ctx.body(), SocietyIdRequest.class);

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

}
