package uk.ac.port.setap.team6c;

import com.google.gson.Gson;
import io.javalin.Javalin;
import uk.ac.port.setap.team6c.authentication.Authentication;

public class Main {

    public static Gson GSON = new Gson();

    public static void main(String[] args) {
        Javalin app = Javalin.create();

        app.get("/", ctx -> ctx.result("Hello, world!"));
        app.get("/login", Authentication::login);

        app.start(7071);
    }

}
