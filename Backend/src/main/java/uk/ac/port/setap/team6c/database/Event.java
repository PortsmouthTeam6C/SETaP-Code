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
public class Event {

    private final int eventId;
    private final Instant date;
    private final String location;
    private final String name;
    private final String description;
    private final int price;
    private final String image;

    /**
     * Get a list of all events belonging to the specified society
     * @param societyId The id of the society
     * @return An {@link EventCollection} containing all the events
     */
    public static @Nullable EventCollection getAllEvents(int societyId) {
        List<Integer> events = new ArrayList<>();
        try (Connection connection = DatabaseManager.getSource().getConnection()) {
            Optional<ResultSet> optionalResultSet = DatabaseManager.populateAndExecute(
                    connection,
                    "select eventid from events where societyid = ?",
                    societyId);

            if (optionalResultSet.isEmpty())
                return null;

            ResultSet resultSet = optionalResultSet.get();
            do {
                events.add(resultSet.getInt("eventid"));
            } while (resultSet.next());

            return new EventCollection(events);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get an event from the event's unique eventId
     * @param eventId The event's eventId
     * @return The event, or null if the eventId was not found
     */
    public static @Nullable Event get(int eventId) {
        return get("select * from events where eventId = ?", eventId);
    }

    /**
     * Retrieve an event from the database using a query string and any set of parameters
     * @param query The query string
     * @param params The set of parameters
     * @return The event, or null if no result found or error
     */
    private static @Nullable Event get(String query, Object... params) {
        try (Connection connection = DatabaseManager.getSource().getConnection()) {
            Optional<ResultSet> optionalResultSet = DatabaseManager.populateAndExecute(connection, query, params);
            if (optionalResultSet.isEmpty())
                return null;

            ResultSet resultSet = optionalResultSet.get();

            return new Event(
                    resultSet.getInt("eventId"),
                    resultSet.getTimestamp("date").toInstant(),
                    resultSet.getString("location"),
                    resultSet.getString("name"),
                    resultSet.getString("description"),
                    resultSet.getInt("price"),
                    resultSet.getString("image")
            );
        } catch (Exception e) {
            return null;
        }
    }

}
