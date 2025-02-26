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
public class University {

    @Getter(AccessLevel.PACKAGE)
    private int universityId;
    private String universityName;
    private String emailDomain;
    private String theming;

    public University(String universityName, String emailDomain, String theming) throws UniversityAlreadyExistsException {
        this.universityName = universityName;
        this.emailDomain = emailDomain;
        this.theming = theming;

        try {
            DatabaseManager.createConnection(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement("insert into university (universityName, emailDomain, theming) values (?, ?, ?) returning universityId");
                preparedStatement.setString(1, universityName);
                preparedStatement.setString(2, emailDomain);
                preparedStatement.setString(3, theming);
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                this.universityId = resultSet.getInt("universityId");
            });
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new UniversityAlreadyExistsException();
        }
    }

    public University(String emailDomain) throws UniversityNotFoundException {
        try {
            DatabaseManager.createConnection(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement("select * from university where emailDomain = ?");
                preparedStatement.setString(1, emailDomain);
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                this.universityId = resultSet.getInt("universityId");
                this.universityName = resultSet.getString("universityName");
                this.emailDomain = resultSet.getString("emailDomain");
                this.theming = resultSet.getString("theming");
            });
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new UniversityNotFoundException();
        }
    }

    public @NotNull SocietyCollection getSocieties() {
        List<Integer> societies = new ArrayList<>();
        try {
            DatabaseManager.createConnection(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement("select societyid from society where universityid = ?");
                preparedStatement.setInt(1, universityId);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next())
                    societies.add(resultSet.getInt("societyid"));
            });
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return new SocietyCollection(societies);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof University && ((University) obj).universityId == universityId;
    }

    public static class UniversityAlreadyExistsException extends Exception {}
    public static class UniversityNotFoundException extends Exception {}

}
