package uk.ac.port.setap.team6c;

import com.google.gson.Gson;
import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.Javalin;
import uk.ac.port.setap.team6c.authentication.AuthManager;
import uk.ac.port.setap.team6c.database.*;

import java.util.List;

public class Main {

    public static Gson GSON = new Gson();
    public static Dotenv ENV = Dotenv.configure()
            .directory("src/main/resources")
            .filename(".env")
            .load();

    public static void main(String[] args) {
        DatabaseManager.resetDatabase(); // Uncomment if you need to create the database
        Javalin app = Javalin.create();

        app.get("/", ctx -> ctx.result("Hello, world!"));
        app.get("/login", AuthManager::login);

        app.start(7071);
    }

}
