package uk.ac.port.setap.team6c.database;

import org.apache.commons.dbcp2.BasicDataSource;
import org.jetbrains.annotations.NotNull;
import uk.ac.port.setap.team6c.Main;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final BasicDataSource source;

    static {
        source = new BasicDataSource();
        source.setDriverClassName("org.postgresql.Driver");
        source.setUsername(Main.ENV.get("DB_USER"));
        source.setPassword(Main.ENV.get("DB_PASS"));
        source.setUrl("jdbc:postgresql://localhost:" + Main.ENV.get("DB_PORT") + "/" + Main.ENV.get("DB_NAME"));
    }

    /**
     * Creates a temporary connection to the database
     * @param function The function to run with the connection
     * @throws SQLException A SQLException will be returned if something goes wrong with the database query
     */
    public static void createConnection(@NotNull ConnectionConsumer function) throws SQLException {
        Connection connection = source.getConnection();
        function.accept(connection);
        connection.close();
    }

    /**
     * A functional interface for accepting a connection, used by {@link #createConnection(ConnectionConsumer)}
     */
    @FunctionalInterface
    public interface ConnectionConsumer {
        void accept(Connection connection) throws SQLException;
    }

    /**
     * Initializes the database with the required tables
     */
    public static void initializeDatabase() {
        try {
            createConnection(connection -> {
                Statement statement = connection.createStatement();
                statement.execute(
                    "create table if not exists university (" +
                        "universityId serial primary key," +
                        "universityName varchar(100) not null," +
                        "emailDomain varchar(100) unique not null check (emailDomain ~ $$@[\\w\\.]{1,}$$)," +
                        "theming text" +
                    ");" +

                    "create table if not exists society (" +
                        "societyId serial primary key," +
                        "universityId int references university(universityId)," +
                        "societyName varchar(100) not null," +
                        "societyDescription text not null," +
                        "societyPicture text not null," +
                        "maxSize int," +
                        "isPaid boolean not null" +
                    ");" +

                    "create table if not exists users (" +
                        "userId serial primary key," +
                        "universityId int references university(universityId)," +
                        "username varchar(32) not null," +
                        "email varchar(255) unique not null," +
                        "password char(60) not null," +
                        "profilePicture text not null," +
                        "isAdministrator boolean not null," +
                        "settings text not null" +
                    ");" +

                    "create table if not exists sessionToken (" +
                        "token char(36) primary key," +
                        "userid int references users(userid)," +
                        "expiry timestamp not null" +
                    ");" +

                    "create table if not exists societyMember (" +
                        "userId int references users(userId)," +
                        "societyId int references society(societyId)," +
                        "primary key (userId, societyId)," +
                        "isManager boolean not null" +
                    ");" +

                    "create table if not exists message (" +
                        "messageId serial primary key," +
                        "userId int references users(userId)," +
                        "societyId int references society(societyId)," +
                        "messageContent text not null," +
                        "timestamp timestamp not null," +
                        "isPinned boolean not null" +
                    ");" +

                    "create table if not exists events (" +
                        "eventId serial primary key," +
                        "userId int references users(userId)," +
                        "startTimestamp timestamp not null," +
                        "endTimestamp timestamp not null," +
                        "createdTimestamp timestamp not null," +
                        "location varchar(255)," +
                        "name varchar(255) not null," +
                        "description text not null" +
                    ");" +

                    "create table if not exists societyEvent (" +
                        "societyId int references society(societyId)," +
                        "eventId int references events(eventId)," +
                        "primary key (societyId, eventId)" +
                    ");"
                );
                statement.close();
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
