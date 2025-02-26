package uk.ac.port.setap.team6c.database;

import lombok.AccessLevel;
import lombok.Getter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

@Getter
public class Event {

    @Getter(AccessLevel.PACKAGE)
    private int eventId;
    @Getter(AccessLevel.PACKAGE)
    private int userid;
    private Instant StartTimestamp;
    private Instant EndTimestamp;
    private Instant CreationTimestamp;
    private String Location;
    private String Name;
    private String Description;

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

    public User getCreator() throws User.UnknownUseridException {
        return new User(userid);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Event && ((Event) obj).eventId == eventId;
    }

    public static class UnknownEventException extends Exception {}

}

