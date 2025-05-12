package uk.ac.port.setap.team6c.routes.universities;

import io.javalin.http.Context;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;
import uk.ac.port.setap.team6c.Main;
import uk.ac.port.setap.team6c.database.University;
import uk.ac.port.setap.team6c.database.User;
import uk.ac.port.setap.team6c.routes.UserTokenRequest;

import java.util.UUID;

public class Universities {

    public static void getUniversity(@NotNull Context ctx) {
        UserTokenRequest request = Main.GSON.fromJson(ctx.body(), UserTokenRequest.class);

        User user;
        try {
            user = new User(UUID.fromString(request.token()), request.expiry());
        } catch (Exception ignored) {
            throw new UnauthorizedResponse();
        }

        University university;
        try {
            university = user.getUniversity();
        } catch (Exception ignored) {
            throw new InternalServerErrorResponse();
        }

        ctx.result(Main.GSON.toJson(new UniversityResponse(university.getUniversityName(), university.getTheming())));
    }

}
