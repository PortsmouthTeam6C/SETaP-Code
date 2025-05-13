package uk.ac.port.setap.team6c.database;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Function;

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
        return get("select userid from sessiontoken where userid = ?", userId);
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
                   "where st.token = ? and st.expiry > ?", token, expiry);
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
            return null;
        }
    }

}
