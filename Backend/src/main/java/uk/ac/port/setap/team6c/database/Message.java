package uk.ac.port.setap.team6c.database;

import lombok.AccessLevel;
import lombok.Getter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

@Getter
public class Message {

    @Getter(AccessLevel.PACKAGE)
    int messageId;
    @Getter(AccessLevel.PACKAGE)
    int userId;
    @Getter(AccessLevel.PACKAGE)
    int societyId;
    String messageContent;
    Instant timestamp;
    boolean isPinned;

    protected Message(int messageId) throws MessageDoesNotExistException {
        try {
            DatabaseManager.createConnection(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement("select * from message where messageId = ?");
                preparedStatement.setInt(1, messageId);
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                this.messageId = resultSet.getInt("messageid");
                this.societyId = resultSet.getInt("societyid");
                this.userId = resultSet.getInt("userid");
                this.messageContent = resultSet.getString("messagecontent");
                this.timestamp = resultSet.getTimestamp("timestamp").toInstant();
                this.isPinned = resultSet.getBoolean("ispinned");
            });
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new MessageDoesNotExistException();
        }
    }

    public Society getSociety() throws Society.UnknownSocietyException {
        return new Society(societyId);
    }

    public User getSender() throws User.UnknownUseridException {
        return new User(userId);
    }

    public static class MessageDoesNotExistException extends Exception {}

}
