package uk.ac.port.setap.team6c.database;

import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Event {
    @Getter(AccessLevel.PACKAGE)
    private int eventid;
    @Getter(AccessLevel.PACKAGE)
    private int userid;
    private Instant StartTimestamp;
    private Instant EndTimestamp;
    private Instant CreationTimestamp;
    private String Location;
    private String Name;
    private String Description;

    protected Event(int eventid) throws UnknownEventException {
        try{
            DatebaseManager.createconnection(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement("select * from event where eventid = ?");
                preparedStatement.setInt(1,eventid);
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                this.eventid = resultSet.getInt("eventid");
                this.userid = resultSet.getInt("userid");
                this.StartTimestamp = getInstant("StartTimestamp");
                this.EndTimestamp = getInstant("EndTimestamp");
                this.CreationTimestamp = getInstant("CreationTimestamp");
                this.Location = resultSet.getString("Location");
                this.Name = resultSet.getString("Name");
                this.Description = resultSet.getString("Description");
            })
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new UnknownEventException();
        }
    }
    public User getCreator() throws UnknownEventException{
        return new User(userid)
    }
    public static class UnknownEventException extends Exception {}
}

