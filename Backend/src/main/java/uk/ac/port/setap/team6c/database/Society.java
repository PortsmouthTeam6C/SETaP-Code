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

    protected Society(int societyId) throws UnknownSocietyException {
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

    public static class UnknownSocietyException extends Exception {}

}
