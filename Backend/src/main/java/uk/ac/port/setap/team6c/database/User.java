package uk.ac.port.setap.team6c.database;

import lombok.AccessLevel;
import lombok.Getter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Getter
public class User {

    @Getter(AccessLevel.PACKAGE)
    private int userId;
    @Getter(AccessLevel.PACKAGE)
    private int universityId;
    private String username;
    private String email;
    private String password;
    private String profilePicture;
    private boolean isAdministrator;
    private String settings;

    /**
     * Get a user from a provided login token
     * @param token The login token
     * @param timestamp The expiry timestamp
     * @throws UnknownLoginTokenException if the provided login token does not correspond to a user, or if that token is outdated
     */
    public User(UUID token, Instant timestamp) throws UnknownLoginTokenException {
        try {
            DatabaseManager.createConnection(connection -> {
                // Get the userid
                PreparedStatement preparedStatement = connection.prepareStatement("select userid from sessiontoken where token = ? and expiry > ?");
                preparedStatement.setString(1, token.toString());
                preparedStatement.setTimestamp(2, Timestamp.from(timestamp));
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                this.userId = resultSet.getInt("userid");

                // Get the user
                preparedStatement = connection.prepareStatement("select * from users where userid = ?");
                preparedStatement.setInt(1, this.userId);
                resultSet = preparedStatement.executeQuery();
                resultSet.next();
                this.universityId = resultSet.getInt("universityid");
                this.username = resultSet.getString("username");
                this.email = resultSet.getString("email");
                this.password = resultSet.getString("password");
                this.profilePicture = resultSet.getString("profilepicture");
                this.isAdministrator = resultSet.getBoolean("isadministrator");
                this.settings = resultSet.getString("settings");
            });
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new UnknownLoginTokenException();
        }
    }

    /**
     * Get a user from a provided email
     * <p>
     * <b>Users retrieved via this constructor are NOT automatically authenticated.
     * The user must be authenticated using {@link User#getPassword()} before use.</b>
     *
     * @param email the user's email
     * @throws UnknownEmailException if the provided email does not correspond to a user
     */
    public User(String email) throws UnknownEmailException {
        try {
            DatabaseManager.createConnection(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement("select * from users where email = ?");
                preparedStatement.setString(1, email);
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                this.userId = resultSet.getInt("userid");
                this.universityId = resultSet.getInt("universityid");
                this.username = resultSet.getString("username");
                this.email = resultSet.getString("email");
                this.password = resultSet.getString("password");
                this.profilePicture = resultSet.getString("profilepicture");
                this.isAdministrator = resultSet.getBoolean("isadministrator");
                this.settings = resultSet.getString("settings");
            });
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new UnknownEmailException();
        }
    }

    /**
     * Get a user from a provided user id
     * <p>
     * <b>Users retrieved via this constructor are NOT automatically authenticated.
     * The user must be authenticated using {@link User#getPassword()} before use.</b>
     *
     * @param userId the user's unique id
     * @throws UnknownUseridException if the provided user id does not correspond to a user
     */
    protected User(int userId) throws UnknownUseridException {
        try {
            DatabaseManager.createConnection(connection -> {
                // Get the userid
                PreparedStatement preparedStatement = connection.prepareStatement("select * from users where userid = ?");
                preparedStatement.setInt(1, userId);
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                this.userId = resultSet.getInt("userid");
                this.universityId = resultSet.getInt("universityid");
                this.username = resultSet.getString("username");
                this.email = resultSet.getString("email");
                this.password = resultSet.getString("password");
                this.profilePicture = resultSet.getString("profilepicture");
                this.isAdministrator = resultSet.getBoolean("isadministrator");
                this.settings = resultSet.getString("settings");
            });
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new UnknownUseridException();
        }
    }

    public User(University university, String username, String email, String password, String profilePicture, boolean isAdministrator, String settings) throws AccountAlreadyExistsException {
        this.universityId = university.getUniversityId();
        this.username = username;
        this.email = email;
        this.password = password;
        this.profilePicture = profilePicture;
        this.isAdministrator = isAdministrator;
        this.settings = settings;

        try {
            DatabaseManager.createConnection(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement("insert into users (universityId, username, email, password, profilePicture, isAdministrator, settings) values (?, ?, ?, ?, ?, ?, ?) returning userId");
                preparedStatement.setInt(1, this.universityId);
                preparedStatement.setString(2, this.username);
                preparedStatement.setString(3, this.email);
                preparedStatement.setString(4, this.password);
                preparedStatement.setString(5, this.profilePicture);
                preparedStatement.setBoolean(6, this.isAdministrator);
                preparedStatement.setString(7, this.settings);
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                this.userId = resultSet.getInt("userId");
            });
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new AccountAlreadyExistsException();
        }
    }

    public void assignSessionToken(UUID token, Instant expiry) throws SessionTokenCouldNotBeCreatedException {
        try {
            DatabaseManager.createConnection(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement("insert into sessionToken (token, userid, expiry) values (?, ?, ?)");
                preparedStatement.setString(1, token.toString());
                preparedStatement.setInt(2, userId);
                preparedStatement.setTimestamp(3, Timestamp.from(expiry));
                preparedStatement.execute();
            });
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new SessionTokenCouldNotBeCreatedException();
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof User && ((User) obj).userId == userId;
    }

    public static class UnknownLoginTokenException extends Exception {}
    public static class UnknownEmailException extends Exception {}
    public static class UnknownUseridException extends Exception {}
    public static class AccountAlreadyExistsException extends Exception {}
    public static class SessionTokenCouldNotBeCreatedException extends Exception {}

}
