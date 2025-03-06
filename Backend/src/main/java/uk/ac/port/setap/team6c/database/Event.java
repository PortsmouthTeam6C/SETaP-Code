package uk.ac.port.setap.team6c.database;

import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Event {

    private int eventId;
    private int userid;
    private Instant StartTimestamp;
    private Instant EndTimestamp;
    private Instant CreationTimestamp;
    private String Location;
    private String Name;
    private String Description;

    /**
     * Get an event from an event id
     * @param eventId The event id
     * @throws UnknownEventException if the provided event id does not correspond to an event
     */
    protected Event(int eventId) throws UnknownEventException {
        try{
            DatabaseManager.createConnection(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement("select * from events where eventid = ?");
                preparedStatement.setInt(1, eventId);
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                this.eventId = resultSet.getInt("eventid");
                this.userid = resultSet.getInt("userid");
                this.StartTimestamp = resultSet.getTimestamp("StartTimestamp").toInstant();
                this.EndTimestamp = resultSet.getTimestamp("EndTimestamp").toInstant();
                this.CreationTimestamp = resultSet.getTimestamp("CreationTimestamp").toInstant();
                this.Location = resultSet.getString("Location");
                this.Name = resultSet.getString("Name");
                this.Description = resultSet.getString("Description");
            });
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new UnknownEventException();
        }
    }

    /**
     * Get the creator of the event
     * @return The creator of the event
     * @throws User.UnknownUseridException if the creator of the event does not exist
     */
    public User getCreator() throws User.UnknownUseridException {
        return new User(userid);
    }
    public @NotNull UserCollection getUsers () throws UnknownEventException {
        List<Integer> userids = new ArrayList<>();
        try {
            DatabaseManager.createConnection(Connection -> {
                PreparedStatement preparedStatement = Connection.prepareStatement("select userid from eventuser where eventid = ?");
                preparedStatement.setInt(1, eventId);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    userids.add(resultSet.getInt("userid"));
                }
            });
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return new UserCollection(userids);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Event && ((Event) obj).eventId == eventId;
    }

    public static class UnknownEventException extends Exception {}

}

