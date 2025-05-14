package uk.ac.port.setap.team6c.routes;

import io.javalin.http.Context;
import io.javalin.http.OkResponse;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;
import uk.ac.port.setap.team6c.Main;
import uk.ac.port.setap.team6c.database.*;
import uk.ac.port.setap.team6c.routes.genericrequests.IdRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MessageManager {

    /**
     * Route to get all chat messages
     * @param ctx The request context
     */
    public static void getAllMessages(@NotNull Context ctx) {
        IdRequest request = Main.GSON.fromJson(ctx.body(), IdRequest.class);

        MessageCollection messageCollection = Message.getAllMessages(request.id());
        List<MessageResponse> messages = messageCollectionToResponseList(messageCollection);
        ctx.result(Main.GSON.toJson(messages));
    }

    /**
     * Route to send a message
     * @param ctx The request context
     */
    public static void sendMessage(@NotNull Context ctx) {
        MessageRequest request = Main.GSON.fromJson(ctx.body(), MessageRequest.class);

        // If the user doesn't exist, they're not authorized to send messages
        User user = User.get(request.token, request.expiry);
        if (user == null)
            throw new UnauthorizedResponse();

        // If the user is not a member of the society, they can't send a message in it
        if (!user.hasJoinedSociety(request.societyId))
            throw new UnauthorizedResponse();

        Message.create(user.getUserId(), request.societyId, request.content, Instant.now());
        throw new OkResponse();
    }

    /**
     * Converts a MessageCollection into a list of MessageResponse objects, which can be sent to the user
     * @param messageCollection The collection of messages to convert
     * @return The list of MessageResponse objects
     */
    private static @NotNull List<MessageResponse> messageCollectionToResponseList(@NotNull MessageCollection messageCollection) {
        List<MessageResponse> messages = new ArrayList<>();
        for (Message message : messageCollection) {
            User user = User.get(message.getUserId());
            if (user != null)
                messages.add(new MessageResponse(message.getMessageId(), message.getUserId(), message.getSocietyId(),
                        user.getUsername(), user.getProfilePicture(), message.getTimestamp(), message.getContent()));
        }
        return messages;
    }

    private record MessageRequest(String token, Instant expiry, int societyId, String content) {}

    private record MessageResponse(int messageId, int userId, int societyId, String username, String profilePicture,
                                   Instant timestamp, String content) {}

}
