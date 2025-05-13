package uk.ac.port.setap.team6c.database;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class is used to access the database indirectly when getting, creating, or querying societies.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Society {

    private final int societyId;
    private final int universityId;
    private final String societyName;
    private final String description;
    private final String societyPicture;

    /**
     * Get a list of all societies belonging to the specified university
     * @param universityId The id of the university
     * @return A {@link SocietyCollection} containing all the societies
     */
    @Contract("_ -> new")
    public static @NotNull SocietyCollection getAllSocieties(int universityId) {
        List<Integer> societies = new ArrayList<>();
        try (Connection connection = DatabaseManager.getSource().getConnection()) {
            Optional<ResultSet> optionalResultSet = DatabaseManager.populateAndExecute(
                    connection,
                    "select societyid from society where universityid = ?",
                    universityId);

            if (optionalResultSet.isEmpty())
                return new SocietyCollection(societies);

            ResultSet resultSet = optionalResultSet.get();
            do {
                societies.add(resultSet.getInt("societyid"));
            } while (resultSet.next());

            return new SocietyCollection(societies);
        } catch (Exception e) {
            return new SocietyCollection(societies);
        }
    }

    /**
     * Get a society from the society's unique societyId
     * @param societyId The society's societyId
     * @return The society, or null if the societyId was not found
     */
    public static @Nullable Society get(int societyId) {
        return get("select * from society where societyId = ?", societyId);
    }

    /**
     * Retrieve a society from the database using a query string and any set of parameters
     * @param query The query string
     * @param params The set of parameters
     * @return The society, or null if no result found or error
     */
    private static @Nullable Society get(String query, Object... params) {
        try (Connection connection = DatabaseManager.getSource().getConnection()) {
            Optional<ResultSet> optionalResultSet = DatabaseManager.populateAndExecute(connection, query, params);
            if (optionalResultSet.isEmpty())
                return null;

            ResultSet resultSet = optionalResultSet.get();

            return new Society(
                    resultSet.getInt("societyId"),
                    resultSet.getInt("universityId"),
                    resultSet.getString("societyName"),
                    resultSet.getString("description"),
                    resultSet.getString("societyPicture")
            );
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Makes a user join a society
     * @param userId The user that will join the society
     * @param societyId The society they will join
     */
    public static void join(int userId, int societyId) {
        try {
            Connection connection = DatabaseManager.getSource().getConnection();
            String query = "insert into societymember (societyid, userid) values (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, societyId);
            preparedStatement.setInt(2, userId);
            preparedStatement.execute();
            connection.close();
        } catch (Exception ignored) {

        }
    }

}
