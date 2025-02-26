package uk.ac.port.setap.team6c;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;
import uk.ac.port.setap.team6c.authentication.AuthManager;
import uk.ac.port.setap.team6c.database.DatabaseManager;
import uk.ac.port.setap.team6c.gson.InstantTypeAdapter;

import java.time.Instant;

public class Main {

    public static Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
            .create();
    public static Dotenv ENV = Dotenv.configure()
            .directory("src/main/resources")
            .filename(".env")
            .load();

    public static void main(String[] args) {
        DatabaseManager.resetDatabase(); // Uncomment if you need to create the database
        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(CorsPluginConfig.CorsRule::anyHost);
            });
        });

        app.get("/", ctx -> ctx.result("Hello, world!"));
        app.post("/login", AuthManager::login);

        app.start(7071);
    }

}
