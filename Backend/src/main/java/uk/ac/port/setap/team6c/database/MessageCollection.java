package uk.ac.port.setap.team6c.database;

import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class MessageCollection implements Iterable<Message> {

    private final List<Integer> messageIds = new ArrayList<>();

    private MessageCollection() {

    }

    /**
     * Convert this {@link MessageCollection} to a list of messages
     * <p>
     * Note that this method will access the database for each message in the collection and will create an instance of
     * {@link Message} for each message. This can be an expensive operation if the collection is large. The size of the
     * collection should be checked before calling this method using {@link MessageCollection#size()}. Prefer using the
     * iterator returned by {@link MessageCollection#iterator()} if you only need to iterate over the collection as this
     * will lazy-load the messages from the database as they are encountered.
     * <p>
     * If the message does not exist in the database, the message will not be included in the list. This may result in
     * the list being smaller than the collection in rare cases.
     * @return A list of messages
     */
    public List<Message> toList() {
        List<Message> messages = new ArrayList<>();
        for (int messageId : messageIds) {
            try {
                messages.add(new Message(messageId));
            } catch (Message.MessageDoesNotExistException exception) {
                exception.printStackTrace();
            }
        }
        return messages;
    }

    /**
     * Get the number of messages in the collection
     * @return The number of messages in the collection
     */
    public int size() {
        return messageIds.size();
    }

    public MessageCollection containing(String messageContent) {
        // Todo: impl
    }

    public MessageCollection sentBy(User user) {
        // Todo: impl
    }

    public MessageCollection sentIn(Society society) {
        // Todo: impl
    }

    public MessageCollection betweenTimestamps(Instant start, Instant end) {
        // Todo: impl
    }

    /**
     * Get all messages sent in a society's chat room
     * @param societyId The society to get messages for
     * @return A MessageCollection containing all messages sent to the society's chat room
     */
    public static @NotNull MessageCollection fromSociety(int societyId) {
        MessageCollection messageCollection = new MessageCollection();
        try {
            DatabaseManager.createConnection(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement("select messageid from message where societyid = ?");
                preparedStatement.setInt(1, societyId);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    messageCollection.messageIds.add(resultSet.getInt("messageId"));
                }
            });
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return messageCollection;
    }

    /**
     * Get all messages sent by a user
     * @param user The user to get messages from
     * @return A MessageCollection containing all messages sent by the user
     */
    public static @NotNull MessageCollection fromUser(User user) {
        MessageCollection messageCollection = new MessageCollection();
        try {
            DatabaseManager.createConnection(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement("select messageid from message where userid = ?");
                preparedStatement.setInt(1, user.getUserId());
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    messageCollection.messageIds.add(resultSet.getInt("messageId"));
                }
            });
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return messageCollection;
    }

    public class MessageCollectionIterator implements Iterator<Message> {
        private final Iterator<Integer> messageIdIterator = messageIds.iterator();

        @Override
        public boolean hasNext() {
            return messageIdIterator.hasNext();
        }

        @Override
        public Message next() {
            try {
                return new Message(messageIdIterator.next());
            } catch (Message.MessageDoesNotExistException exception) {
                exception.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public @NotNull Iterator<Message> iterator() {
        return new MessageCollectionIterator();
    }

    @Override
    public void forEach(Consumer<? super Message> action) {
        Iterable.super.forEach(action);
    }

}
