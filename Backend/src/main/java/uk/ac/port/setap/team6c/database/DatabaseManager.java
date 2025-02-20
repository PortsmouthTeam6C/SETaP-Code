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

    public static void populateDatabase() {
        try {
            createConnection(connection -> {
                Statement statement =  createStatement();
                statement.execute(
                    "insert into university (universityName, emailDomain, theming) values"
                    "('University of Example', 'example.edu', 'Green and Gold'),"
                    "('Sample University', 'sample.edu', 'Blue and White'),"
                    "('Tech Institute', 'tech.edu', 'Red and Black');"

                    "insert into society (universityId, societyName, societyDescription, societyPicture, maxSize, isPaid) values"
                    "(1, 'Computer Science Society', 'A society for tech enthusiasts and developers', 'cs_society_picture.jpg', 200, false),"
                    "(2, 'Drama Club', 'A society focused on theater arts and performances', 'drama_club_picture.jpg', 100, false),"
                    "(3, 'Science and Innovation', 'A society dedicated to scientific discoveries and innovation', 'science_innovation_picture.jpg', 150, true);"

                    "insert into users (universityId, username, email, password, profilePicture, isAdministrator, settings) values"
                    "(1, 'johndoe', 'johndoe@example.edu', 'passwordhash', 'profile_johndoe.jpg', false, '{"theme":"light","notifications":true}'),"
                    "(2, 'janedoe', 'janedoe@sample.edu', 'passwordhash', 'profile_janedoe.jpg', false, '{"theme":"dark","notifications":false}'),"
                    "(3, 'samuser', 'samuser@tech.edu', 'passwordhash', 'profile_samuser.jpg', true, '{"theme":"light","notifications":true}');"

                    "insert into sessionToken (token, userid, expiry) values"
                    "('d4f7d8c6-6a8f-4c12-b914-7b6f20d1e8fb', 1, '2025-02-28 12:00:00'),"
                    "('3b6f9bc8-8a65-4622-92f8-71b1d8faed3c', 2, '2025-02-25 09:00:00'),"
                    "('ea2a6b1d-6a78-4a69-8a1b-e5c97b81d004', 3, '2025-02-30 15:00:00');"

                    "insert into societyMember (userId, societyId, isManager) values"
                    "(1, 1, true),"
                    "(2, 2, false),"
                    "(3, 3, true),"
                    "(1, 3, false);"

                    "insert into message (userId, societyId, messageContent, timestamp, isPinned) values"
                    "(1, 1, 'Welcome to the Computer Science Society!', '2025-02-15 10:00:00', true),"
                    "(2, 2, 'Join us for an exciting drama performance next week!', '2025-02-18 14:30:00', false),"
                    "(3, 3, 'We have an exciting new research opportunity available!', '2025-02-19 11:45:00', true);"

                    "insert into events (userId, startTimestamp, endTimestamp, createdTimestamp, location, name, description) values"
                    "(1, '2025-03-01 09:00:00', '2025-03-01 12:00:00', '2025-02-10 15:00:00', 'Room 101', 'Tech Talk on AI', 'Join us for a deep dive into Artificial Intelligence.'),"
                    "(2, '2025-02-28 18:00:00', '2025-02-28 21:00:00', '2025-02-15 10:00:00', 'Auditorium', 'Drama Performance: Romeo and Juliet', 'Watch our talented actors perform the classic play, Romeo and Juliet.'),"
                    "(3, '2025-03-05 13:00:00', '2025-03-05 16:00:00', '2025-02-20 09:00:00', 'Lab 202', 'Innovation Challenge', 'Participate in our innovation challenge and showcase your ideas.');"

                    "insert into societyEvent (societyId, eventId) values"
                    "(1, 1),"
                    "(2, 2),"
                    "(3, 3);"
                )
                statement.close()

            })
        } catch (SQLException e) {
            e.printStackTrace()
        }
    }

}
