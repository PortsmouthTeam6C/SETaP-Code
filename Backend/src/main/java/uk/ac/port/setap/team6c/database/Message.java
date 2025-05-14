package uk.ac.port.setap.team6c.database;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class is used to access the database indirectly when getting, creating, or querying messages.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Message {

    private final int messageId;
    private final int userId;
    private final int societyId;
    private final String content;
    private final Instant timestamp;

    /**
     * Get a list of all messages belonging to the specified society
     * @param societyId The id of the society
     * @return A {@link MessageCollection} containing all the messages
     */
    @Contract("_ -> new")
    public static @NotNull MessageCollection getAllMessages(int societyId) {
        List<Integer> messages = new ArrayList<>();
        try (Connection connection = DatabaseManager.getSource().getConnection()) {
            Optional<ResultSet> optionalResultSet = DatabaseManager.populateAndExecute(
                    connection,
                    "select messageid from message where societyid = ?",
                    societyId);

            if (optionalResultSet.isEmpty())
                return new MessageCollection(messages);

            ResultSet resultSet = optionalResultSet.get();
            do {
                messages.add(resultSet.getInt("messageid"));
            } while (resultSet.next());

            return new MessageCollection(messages);
        } catch (Exception e) {
            return new MessageCollection(messages);
        }
    }

    /**
     * Retrieve a message from the database using its unique messageId
     * @param messageId The message's messageId
     * @return The message, or null if the messageId was not found
     */
    public static Message get(int messageId) {
        return get("select * from message where messageId = ?", messageId);
    }

    /**
     * Retrieve messages from the database using a query string and any set of parameters
     * @param query The query string
     * @param params The set of parameters
     * @return The messages, or null if no result found or error
     */
    private static @Nullable Message get(String query, Object... params) {
        try (Connection connection = DatabaseManager.getSource().getConnection()) {
            Optional<ResultSet> optionalResultSet = DatabaseManager.populateAndExecute(connection, query, params);
            if (optionalResultSet.isEmpty())
                return null;

            ResultSet resultSet = optionalResultSet.get();

            return new Message(
                    resultSet.getInt("messageId"),
                    resultSet.getInt("userId"),
                    resultSet.getInt("societyId"),
                    resultSet.getString("content"),
                    resultSet.getTimestamp("timestamp").toInstant()
            );
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Creates a message and adds it to the database
     * @param userId The user who sent the message
     * @param societyId The society it was sent in
     * @param content The content of the message
     * @param timestamp The time the message was sent at
     */
    public static void create(int userId, int societyId, String content, Instant timestamp) {
        try {
            Connection connection = DatabaseManager.getSource().getConnection();
            String query = "insert into message (userid, societyid, content, timestamp) values (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, societyId);
            preparedStatement.setString(3, content);
            preparedStatement.setTimestamp(4, Timestamp.from(timestamp));
            preparedStatement.execute();
            connection.close();
        } catch (Exception ignored) {

        }
    }

}
