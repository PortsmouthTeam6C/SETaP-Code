package uk.ac.port.setap.team6c;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;
import uk.ac.port.setap.team6c.routes.authentication.AuthManager;
import uk.ac.port.setap.team6c.database.DatabaseManager;
import uk.ac.port.setap.team6c.gson.InstantTypeAdapter;
import uk.ac.port.setap.team6c.routes.messages.Messages;
import uk.ac.port.setap.team6c.routes.societies.Societies;

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

        app.post("/user/login", AuthManager::login);
        app.post("/user/signup/reserve-account", AuthManager::createAccount);
        app.post("/societies/all", Societies::getAllSocieties);
        app.post("/societies/joined", Societies::getJoinedSocieties);
        app.post("/societies/info", Societies::getSocietyInfo);
        app.post("/societies/messages", Messages::getMessagesFromSociety);

        app.start(7071);
    }

}
