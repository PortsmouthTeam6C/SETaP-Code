package uk.ac.port.setap.team6c.database;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Optional;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class University {
    
    private final int universityId;
    private final String universityName;
    private final String universityPicture;
    private final String emailDomain;
    private final String theming;

    public static University get(int universityId) {
        return get("select * from university where universityid = ?", universityId);
    }

    public static University get(String emailDomain) {
        return get("select * from university where emailDomain = ?", emailDomain);
    }

    /**
     * Retrieve a university from the database using a query string and any set of parameters
     * @param query The query string
     * @param params The set of parameters
     * @return The university, or null if no result found or error
     */
    private static @Nullable University get(String query, Object... params) {
        try (Connection connection = DatabaseManager.getSource().getConnection()) {
            Optional<ResultSet> optionalResultSet = DatabaseManager.populateAndExecute(connection, query, params);
            if (optionalResultSet.isEmpty())
                return null;

            ResultSet resultSet = optionalResultSet.get();

            return new University(
                resultSet.getInt("universityId"),
                resultSet.getString("universityName"),
                resultSet.getString("universityPicture"),
                resultSet.getString("emailDomain"),
                resultSet.getString("theming")
            );
        } catch (Exception e) {
            return null;
        }
    }

}
