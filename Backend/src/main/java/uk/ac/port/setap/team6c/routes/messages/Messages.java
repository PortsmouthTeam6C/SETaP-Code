package uk.ac.port.setap.team6c.routes.messages;

import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import uk.ac.port.setap.team6c.Main;
import uk.ac.port.setap.team6c.database.Message;
import uk.ac.port.setap.team6c.database.MessageCollection;
import uk.ac.port.setap.team6c.database.MessageCollectionBuilder;
import uk.ac.port.setap.team6c.database.User;

import java.util.List;

public class Messages {

    public static void getMessages(@NotNull Context ctx) {
        MessageRequest request = Main.GSON.fromJson(ctx.body(), MessageRequest.class);

        List<Message> messages = createMessageCollection(request).toList();
        /* Todo: parse messages into a response object
        *   Ideally instead of sending back userId and societyId we should be sending back more useful information about them.
        *   Potentially requires caching database responses for the non-authenticated constructors to reduce query counts.
        *   -
        *   Potential formats for response object:
        *   Easier to parse on the frontend, however larger volume of data per response:
        *   [
        *       { content: "...", timestamp: "...", isPinned: false, senderEmail: "...", senderUsername: "...", senderProfilePicture: "...", societyName: "...", societyId: "..." },
        *       ...
        *   ]
        *   -
        *   Significantly reduced amount of data per response, however slightly harder to parse
        *   {
        *       "messages": [
        *           { content: "...", timestamp: "...", isPinned: false, senderIdx: 0, societyIdx: 0 },
        *           ...
        *       ],
        *       "users": [
        *           { email: "...", username: "...", profilePicture: "..." }
        *           ...
        *       ],
        *       "societies": [
        *           { name: "...", "id": "..." }
        *       ]
        *   }
        *   -
        *   Absolute minimum amount of data per response, however additional requests are required to get more information
        *   [
        *       { content: "...", timestamp: "...", isPinned: false, senderId: 0, societyId: 0 },
        *       ...
        *   ]
        * */
    }

    private static MessageCollection createMessageCollection(@NotNull MessageRequest request) {
        MessageCollectionBuilder messageCollectionBuilder = new MessageCollectionBuilder()
                .containing(request.messageContent())
                .betweenTimestamps(request.after(), request.before())
                .sentIn(request.societyId());

        User sender = null;
        try {
            sender = new User(request.senderEmail());
        } catch (User.UnknownEmailException ignored) {}
        messageCollectionBuilder.sentBy(sender);

        return messageCollectionBuilder.build();
    }

}
