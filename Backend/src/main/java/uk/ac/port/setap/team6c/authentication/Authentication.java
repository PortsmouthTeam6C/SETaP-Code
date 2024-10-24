package uk.ac.port.setap.team6c.authentication;

import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import uk.ac.port.setap.team6c.Main;

import java.util.UUID;

public class Authentication {

    public static void login(Context ctx) {
        Main.GSON.fromJson(ctx.body(), EmailPasswordRequest.class);

       // Todo: Access database & check validity
        boolean isAuthenticated = true;
        if (!isAuthenticated)
            throw new UnauthorizedResponse();

        String token = generateToken();
        ctx.json(new User(token));
    }

    /**
     * Generates an access token which can be used in place of email & password
     * @return The access token
     */
    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
    
}

