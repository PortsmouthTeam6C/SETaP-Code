package uk.ac.port.setap.team6c.database;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import uk.ac.port.setap.team6c.routes.AuthManager;

import java.sql.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * This class is used to access the database indirectly when getting, creating, or querying users.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    private final int userId;
    private final int universityId;
    private final String username;
    private final String email;
    private final String password;
    private final String profilePicture;

    /**
     * Retrieve a user from the database using their unique userid
     * @param userId The user's userid
     * @return The user, or null if the userid was not found
     */
    public static @Nullable User get(int userId) {
        return get("select * from users where userid = ?", userId);
    }

    /**
     * Retrieve a user from the database using a login token and its expiry date
     * @param token The login token
     * @param expiry The expiry date
     * @return The user, or null if the token/expiry date combination was not found
     */
    public static @Nullable User get(String token, Instant expiry) {
        return get("select u.userid, u.universityid, u.username, u.email, u.password, u.profilepicture " +
                   "from users u " +
                   "join sessiontoken st on u.userid = st.userid " +
                   "where st.token = ? and st.expiry = ?", token, Timestamp.from(expiry));
    }

    /**
     * Retrieve a user from the database using their email address
     * @param email The user's email address
     * @return The user, or null if the userid was not found
     */
    public static @Nullable User get(String email) {
        return get("select * from users where email = ?", email);
    }

    /**
     * Retrieve a user from the database using a query string and any set of parameters
     * @param query The query string
     * @param params The set of parameters
     * @return The user, or null if no result found or error
     */
    private static @Nullable User get(String query, Object... params) {
        try (Connection connection = DatabaseManager.getSource().getConnection()) {
            Optional<ResultSet> optionalResultSet = DatabaseManager.populateAndExecute(connection, query, params);
            if (optionalResultSet.isEmpty())
                return null;

            ResultSet resultSet = optionalResultSet.get();

            return new User(
                    resultSet.getInt("userId"),
                    resultSet.getInt("universityId"),
                    resultSet.getString("username"),
                    resultSet.getString("email"),
                    resultSet.getString("password"),
                    resultSet.getString("profilePicture")
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Assign a session token to the user
     * @param token The token to assign
     * @param expiry The expiry of the token
     * @return True if success, false otherwise
     */
    public boolean assignSessionToken(String token, Instant expiry) {
        try {
            Connection connection = DatabaseManager.getSource().getConnection();
            String query = "insert into sessionToken (token, userid, expiry) values (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, token);
            preparedStatement.setInt(2, userId);
            preparedStatement.setTimestamp(3, Timestamp.from(expiry));
            preparedStatement.execute();
            connection.close();
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }

    /**
     * Create a new user object and save it to the database
     * @param universityId The id of the university the user belongs to
     * @param username The user's username
     * @param email The user's email
     * @param hashedPassword The user's password, it must be hashed via {@link AuthManager#hashPassword(String)} before
     *                       use
     * @param profilePicture The user's profile picture
     * @return The created user object
     */
    public static @Nullable User create(int universityId, String username, String email, String hashedPassword,
                                        String profilePicture) {
        try {
            Connection connection = DatabaseManager.getSource().getConnection();
            String query = "insert into users (universityId, username, email, password, profilePicture) " +
                    "values (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, universityId);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, hashedPassword);
            preparedStatement.setString(5, profilePicture);
            preparedStatement.execute();
            connection.close();

            return get(email);
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * Check if the user has joined a given society
     * @param societyId The society to check
     * @return True if they have joined the society, false otherwise
     */
    public boolean hasJoinedSociety(int societyId) {
        try (Connection connection = DatabaseManager.getSource().getConnection()) {
            Optional<ResultSet> optionalResultSet = DatabaseManager.populateAndExecute(
                    connection,
                    "select * from societymember where userid = ? and societyid = ?",
                    userId, societyId
            );
            return optionalResultSet.isPresent();
        } catch (Exception e) {
            return false;
        }
    }

}
