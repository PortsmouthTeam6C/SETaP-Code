package uk.ac.port.setap.team6c.database;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public static @Nullable MessageCollection getAllMessages(int societyId) {
        List<Integer> messages = new ArrayList<>();
        try (Connection connection = DatabaseManager.getSource().getConnection()) {
            Optional<ResultSet> optionalResultSet = DatabaseManager.populateAndExecute(
                    connection,
                    "select messageid from message where societyid = ?",
                    societyId);

            if (optionalResultSet.isEmpty())
                return null;

            ResultSet resultSet = optionalResultSet.get();
            do {
                messages.add(resultSet.getInt("messageid"));
            } while (resultSet.next());

            return new MessageCollection(messages);
        } catch (Exception e) {
            return null;
        }
    }

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
}
