package uk.ac.port.setap.team6c.database;

import lombok.AccessLevel;
import lombok.Getter;
import uk.ac.port.setap.team6c.authentication.AuthManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

/**
 * Represents a user in the database
 */
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
                // Get the userid
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
     * Create a new user
     * @param university The university the user is associated with
     * @param username The user's username
     * @param email The user's email
     * @param password The user's hashed password. This should be hashed using {@link AuthManager#hashPassword(String)}
     * @param profilePicture The user's profile picture
     * @param isAdministrator Whether the user is an administrator
     * @param settings The user's settings
     * @throws AccountAlreadyExistsException if a user with the provided email already exists
     */
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

    /**
     * Save a created session token to the user. The session token should also be returned to the user.
     * @param token The session token
     * @param expiry The token's expiry date
     * @throws SessionTokenCouldNotBeCreatedException if the session token could not be created
     */
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

    /**
     * Exception thrown when a login token is not found
     */
    public static class UnknownLoginTokenException extends Exception {}

    /**
     * Exception thrown when an email is not found
     */
    public static class UnknownEmailException extends Exception {}

    /**
     * Exception thrown when an account already exists
     */
    public static class AccountAlreadyExistsException extends Exception {}

    /**
     * Exception thrown when a session token could not be created
     */
    public static class SessionTokenCouldNotBeCreatedException extends Exception {}

}
