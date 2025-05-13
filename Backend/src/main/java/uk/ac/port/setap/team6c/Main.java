package uk.ac.port.setap.team6c;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;
import uk.ac.port.setap.team6c.database.DatabaseManager;
import uk.ac.port.setap.team6c.gson.InstantTypeAdapter;
import uk.ac.port.setap.team6c.routes.AuthManager;
import uk.ac.port.setap.team6c.routes.EventsManager;
import uk.ac.port.setap.team6c.routes.MessageManager;
import uk.ac.port.setap.team6c.routes.SocietyManager;

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
        DatabaseManager.resetDatabase();
        Javalin app = Javalin.create(config ->
            config.bundledPlugins.enableCors(cors ->
                cors.addRule(CorsPluginConfig.CorsRule::anyHost)
            )
        );

        app.post("/account/create", AuthManager::signup);
        app.post("/account/login/email", AuthManager::login);
        app.post("/account/login/token", AuthManager::loginWithToken);
        app.post("/society/get/all", SocietyManager::getAllSocieties);
        app.post("/society/get/joined", SocietyManager::getAllJoinedSocieties);
        app.post("/society/join", SocietyManager::joinSociety);
        app.post("/chat/get", MessageManager::getAllMessages);
        app.post("/chat/send", MessageManager::sendMessage);
        app.post("/event/get", EventsManager::getAllEvents);
        app.post("event/create", EventsManager::createEvent);

        app.start(7071);
    }

}
