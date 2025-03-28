package uk.ac.port.setap.team6c.database;

import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Stream;

/**
 * A builder for {@link MessageCollection}. All filters are optional & can be combined in any combination.
 */
@NoArgsConstructor
public class MessageCollectionBuilder {

    private String messageContent;
    private Integer userId; // Using boxed integers to allow for null values
    private Integer societyId;
    private Instant start;
    private Instant end;

    /**
     * Filter messages to only include those containing a specific string
     * @param messageContent The string to filter messages by
     * @return This {@link MessageCollectionBuilder} instance
     */
    public MessageCollectionBuilder containing(String messageContent) {
        this.messageContent = messageContent;
        return this;
    }

    /**
     * Filter messages to only include those sent by a specific user
     * @param user The user to filter messages by
     * @return This {@link MessageCollectionBuilder} instance
     */
    public MessageCollectionBuilder sentBy(User user) {
        if (user != null)
            this.userId = user.getUserId();
        return this;
    }

    /**
     * Filter messages to only include those sent by a specific user
     * @param userId The user to filter messages by
     * @return This {@link MessageCollectionBuilder} instance
     */
    public MessageCollectionBuilder sentBy(Integer userId) {
        this.userId = userId;
        return this;
    }

    /**
     * Filter messages to only include those sent in a specific society
     * @param society The society to filter messages by
     * @return This {@link MessageCollectionBuilder} instance
     */
    public MessageCollectionBuilder sentIn(@NotNull Society society) {
        this.societyId = society.getSocietyId();
        return this;
    }

    /**
     * Filter messages to only include those sent in a specific societyId
     * @param societyId The societyId to filter messages by
     * @return This {@link MessageCollectionBuilder} instance
     */
    public MessageCollectionBuilder sentIn(Integer societyId) {
        this.societyId = societyId;
        return this;
    }

    /**
     * Filter messages to only include those sent between two timestamps
     * @param start The start timestamp (inclusive)
     * @param end The end timestamp (inclusive)
     * @return This {@link MessageCollectionBuilder} instance
     */
    public MessageCollectionBuilder betweenTimestamps(Instant start, Instant end) {
        this.start = start;
        this.end = end;
        return this;
    }

    /**
     * Build the {@link MessageCollection} using the filters provided
     * @return The {@link MessageCollection} instance
     */
    public MessageCollection build() {
        List<Integer> messageIds = new ArrayList<>();
        try {
            DatabaseManager.createConnection(connection -> {
                ResultSet resultSet = prepareStatement(connection).executeQuery();
                while (resultSet.next()) {
                    messageIds.add(resultSet.getInt("messageId"));
                }
            });
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return new MessageCollection(messageIds);
    }

    /**
     * Prepare a {@link PreparedStatement} with the filters provided
     * @param connection The connection to prepare the statement on
     * @return The prepared {@link PreparedStatement}
     * @throws SQLException If an error occurs while preparing the statement
     */
    private PreparedStatement prepareStatement(Connection connection) throws SQLException {
        List<Object> parameters = new ArrayList<>();
        StringJoiner query = new StringJoiner("\n");
        query.add("select messageid from message where 1=1"); // where 1=1 is a trick to allow for easy appending of conditions

        // If all fields are null, return all messages
        if (Stream.of(messageContent, userId, societyId, start, end).allMatch(Objects::isNull)) {
            query.add(";");
            return connection.prepareStatement(query.toString());
        }

        if (messageContent != null) {
            query.add("messagecontent like '%?%'");
            parameters.add(messageContent);
        }

        if (userId != null) {
            query.add("userid = ?");
            parameters.add(userId);
        }

        if (societyId != null) {
            query.add("societyid = ?");
            parameters.add(societyId);
        }

        if (start != null) {
            query.add("timestamp >= ?");
            parameters.add(start);
        }

        if (end != null) {
            query.add("timestamp <= ?");
            parameters.add(end);
        }

        query.add(";");
        PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
        for (int i = 0; i < parameters.size(); i++) {
            preparedStatement.setObject(i + 1, parameters.get(i));
        }
        return preparedStatement;
    }

}
