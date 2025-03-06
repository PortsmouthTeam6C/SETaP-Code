package uk.ac.port.setap.team6c.database;

import lombok.AccessLevel;
import lombok.Getter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

@Getter
public class Message {

    int messageId;
    int userId;
    int societyId;
    String messageContent;
    Instant timestamp;
    boolean isPinned;

    /**
     * Get a message from a provided message ID
     * @param messageId The message ID
     * @throws MessageDoesNotExistException if the provided message ID does not correspond to a message
     */
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

    /**
     * Get the society that this message was sent in
     * @return The society that this message was sent in
     * @throws Society.UnknownSocietyException if the society that this message was sent in does not exist
     */
    public Society getSociety() throws Society.UnknownSocietyException {
        return new Society(societyId);
    }

    /**
     * Get the sender of this message
     * @return The sender of this message
     * @throws User.UnknownUseridException if the sender of this message does not exist
     */
    public User getSender() throws User.UnknownUseridException {
        return new User(userId);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Message && ((Message) obj).messageId == messageId;
    }

    public static class MessageDoesNotExistException extends Exception {}

}
