package uk.ac.port.setap.team6c.database;

import lombok.AccessLevel;
import lombok.Getter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Society {

    private int societyId;
    @Getter(AccessLevel.PACKAGE)
    private int universityId;
    private String societyName;
    private String societyDescription;
    private String societyPicture;
    private int maxSize;
    private boolean isPaid;

    /**
     * Get a society from a provided society ID
     * @param societyId The society ID
     * @throws UnknownSocietyException if the provided society ID does not correspond to a society
     */
    public Society(int societyId) throws UnknownSocietyException {
        try {
            DatabaseManager.createConnection(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement("select * from society where societyid = ?");
                preparedStatement.setInt(1, societyId);
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                this.societyId = resultSet.getInt("societyid");
                this.universityId = resultSet.getInt("universityid");
                this.societyName = resultSet.getString("societyname");
                this.societyDescription = resultSet.getString("societydescription");
                this.societyPicture = resultSet.getString("societypicture");
                this.maxSize = resultSet.getInt("maxsize");
                this.isPaid = resultSet.getBoolean("ispaid");
            });
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new UnknownSocietyException();
        }
    }

    /**
     * Get all the users in this society
     * @return A collection of users in this society
     * @throws UnknownSocietyException if the society does not exist
     */
    public UserCollection getUsers() throws UnknownSocietyException {
        List<Integer> userIds = new ArrayList<>();
        try {
            DatabaseManager.createConnection(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement("select userid from societymember where societyid = ?");
                preparedStatement.setInt(1, societyId);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    userIds.add(resultSet.getInt("userid"));
                }
            });
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new UnknownSocietyException();
        }
        return new UserCollection(userIds);
    }
    public UserCollection getManagers() throws UnknownSocietyException {
        List<Integer> userIds = new ArrayList<>();
        try {
            DatabaseManager.createConnection(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement("select userid from societymember where societyid = ? and isManager = true");
                preparedStatement.setInt(1, societyId);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    userIds.add(resultSet.getInt("userid"));
                }
            });
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new UnknownSocietyException();
        }
        return new UserCollection(userIds);
    }

    /**
     * Get all the events in this society
     * @return A collection of events in this society
     * @throws UnknownSocietyException if the society does not exist
     */
    public EventCollection getEvents()  {
        List<Integer> eventIds = new ArrayList<>();
        try {
            DatabaseManager.createConnection(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement("select eventid from societyevent where societyid = ?");
                preparedStatement.setInt(1, societyId);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    eventIds.add(resultSet.getInt("eventId"));
                }
            });
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return new EventCollection(eventIds);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Society && ((Society) obj).societyId == societyId;
    }

    public static class UnknownSocietyException extends Exception {}

}
