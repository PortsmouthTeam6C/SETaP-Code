package uk.ac.port.setap.team6c.routes.messages;

import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import uk.ac.port.setap.team6c.Main;
import uk.ac.port.setap.team6c.database.Message;
import uk.ac.port.setap.team6c.database.MessageCollection;
import uk.ac.port.setap.team6c.database.MessageCollectionBuilder;
import uk.ac.port.setap.team6c.database.User;
import uk.ac.port.setap.team6c.routes.UserResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Messages {

    public static void getMessagesFromSociety(@NotNull Context ctx) {
        MessageRequest request = Main.GSON.fromJson(ctx.body(), MessageRequest.class);
        List<Message> messages = createMessageCollection(request).toList();

        // Convert messages to MessagesResponse
        List<MessageResponse> messageResponses = new ArrayList<>();
        Map<String, UserResponse> userResponses = new HashMap<>();
        for (Message message : messages) {
            User user;
            try {
                user = new User(message.getUserId());
            } catch (Exception ignored) {
                continue;
            }

            messageResponses.add(new MessageResponse(
                    message.getMessageId(),
                    message.getMessageContent(),
                    message.getTimestamp(),
                    message.isPinned(),
                    message.getUserId()
            ));
            userResponses.putIfAbsent(String.valueOf(message.getUserId()), new UserResponse(
                    user.getEmail(),
                    user.getUsername(),
                    user.getProfilePicture()
            ));
        }
        ctx.result(Main.GSON.toJson(new MessagesResponse(messageResponses, userResponses)));
    }

    private static MessageCollection createMessageCollection(@NotNull MessageRequest request) {
        MessageCollectionBuilder messageCollectionBuilder = new MessageCollectionBuilder()
                .containing(request.messageContent())
                .betweenTimestamps(request.after(), request.before())
                .sentIn(request.societyId());

        User sender = null;
        try {
            sender = new User(request.senderEmail());
        } catch (Exception ignored) {}
        messageCollectionBuilder.sentBy(sender);

        return messageCollectionBuilder.build();
    }

}
