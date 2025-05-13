package uk.ac.port.setap.team6c.database;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class is used to access the database indirectly when getting, creating, or querying events.
 */
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
    @Contract("_ -> new")
    public static @NotNull EventCollection getAllEvents(int societyId) {
        List<Integer> events = new ArrayList<>();
        try (Connection connection = DatabaseManager.getSource().getConnection()) {
            Optional<ResultSet> optionalResultSet = DatabaseManager.populateAndExecute(
                    connection,
                    "select eventid from events where societyid = ?",
                    societyId);

            if (optionalResultSet.isEmpty())
                return new EventCollection(events);

            ResultSet resultSet = optionalResultSet.get();
            do {
                events.add(resultSet.getInt("eventid"));
            } while (resultSet.next());

            return new EventCollection(events);
        } catch (Exception e) {
            return new EventCollection(events);
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

    /**
     * Create a new event and save it to the database
     * @param societyId The id of the society to create the event in
     * @param date The date the event will take place on
     * @param location The location of the event
     * @param name The name of the event
     * @param description A brief description of the event
     * @param price The entry price of the event
     * @param image An image associated with the event
     */
    public static void create(int societyId, Instant date, String location, String name, String description, int price,
                             String image) {
        try (Connection connection = DatabaseManager.getSource().getConnection()) {
            String query = "insert into events (date, location, name, description, price, image) " +
                           "values (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setTimestamp(1, Timestamp.from(date));
                preparedStatement.setString(2, location);
                preparedStatement.setString(3, name);
                preparedStatement.setString(4, description);
                preparedStatement.setInt(5, price);
                preparedStatement.setString(6, image);

                preparedStatement.execute();

                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int eventId = generatedKeys.getInt(1);
                        addEventToSociety(eventId, societyId);
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Adds the eventId-societyId combination to the `societyevent` intersection table
     * @param eventId The eventId
     * @param societyId The societyId
     * @throws SQLException If anything goes wrong
     */
    private static void addEventToSociety(int eventId, int societyId) throws SQLException {
        Connection connection = DatabaseManager.getSource().getConnection();
        String query = "insert into societyevent (societyid, eventid) values (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, societyId);
        preparedStatement.setInt(2, eventId);
        preparedStatement.execute();
        connection.close();
    }

}
