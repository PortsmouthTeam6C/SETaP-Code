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
public class Society {

    @Getter(AccessLevel.PACKAGE)
    private int societyId;
    @Getter(AccessLevel.PACKAGE)
    private int universityId;
    private String societyName;
    private String societyDescription;
    private String societyPicture;
    private int maxSize;
    private boolean isPaid;

    private Society(int societyId) throws UnknownSocietyException {
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

    private static @NotNull List<Society> all(University university) {
        List<Society> result = new ArrayList<>();
        try {
            DatabaseManager.createConnection(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement("select societyid from society where universityid = ?");
                preparedStatement.setInt(1, university.getUniversityId());
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                for (int id : List.of(resultSet.getInt("societyid"))) {
                    try {
                        result.add(new Society(id));
                    } catch (UnknownSocietyException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return result;
    }

    public static class UnknownSocietyException extends Exception {}

}
