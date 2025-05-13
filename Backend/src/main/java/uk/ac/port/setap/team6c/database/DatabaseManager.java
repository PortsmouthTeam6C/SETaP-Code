package uk.ac.port.setap.team6c.database;

import lombok.Getter;
import org.apache.commons.dbcp2.BasicDataSource;
import org.jetbrains.annotations.NotNull;
import uk.ac.port.setap.team6c.Main;

import java.sql.*;
import java.util.Optional;

public class DatabaseManager {

    @Getter
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
     * Executes the query with the given parameters on the connection
     * @param connection The connection to execute on
     * @param query The query to execute
     * @param params The parameters to pass to  the query
     * @return A result set if there are results, otherwise null
     * @throws SQLException If anything goes wrong
     */
    static Optional<ResultSet> populateAndExecute(@NotNull Connection connection, String query, @NotNull Object... params) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(query);

        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }

        ResultSet resultSet = preparedStatement.executeQuery();

        if (!resultSet.next())
            return Optional.empty();

        return Optional.of(resultSet);
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
    private static void initializeDatabase() {
        try {
            createConnection(connection -> {
                Statement statement = connection.createStatement();
                statement.execute("""
        create table if not exists university (
            universityId serial primary key,
            universityName varchar(100) not null,
            universityPicture text not null,
            emailDomain varchar(100) unique not null check (emailDomain ~ $$@[\\w\\.]{1,}$$),
            theming text
        );
    
        create table if not exists society (
            societyId serial primary key,
            universityId int references university(universityId),
            societyName varchar(100) not null,
            societyDescription text not null,
            societyPicture text not null
        );
    
        create table if not exists users (
            userId serial primary key,
            universityId int references university(universityId),
            username varchar(32) not null,
            email varchar(255) unique not null,
            password char(60) not null,
            profilePicture text not null
        );
    
        create table if not exists sessionToken (
            token char(36) primary key,
            userid int references users(userid),
            expiry timestamp not null
        );
    
        create table if not exists societyMember (
            userId int references users(userId),
            societyId int references society(societyId),
            primary key (userId, societyId)
        );
    
        create table if not exists message (
            messageId serial primary key,
            userId int references users(userId),
            societyId int references society(societyId),
            content text not null,
            timestamp timestamp not null
        );
    
        create table if not exists events (
            eventId serial primary key,
            date timestamp not null,
            location varchar(255),
            name varchar(255) not null,
            description text not null,
            price int not null,
            image text not null
        );
    
        create table if not exists societyEvent (
            societyId int references society(societyId),
            eventId int references events(eventId),
            primary key (societyId, eventId)
        );
        """);
                statement.close();
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Populates the database with some temp data
     */
    private static void populateDatabase() {
        try {
            createConnection(connection -> {
                Statement statement = connection.createStatement();
                statement.execute(
                    "insert into university (universityName, universityPicture, emailDomain, theming) values" +
                    "('University of Portsmouth', 'https://upload.wikimedia.org/wikipedia/commons/d/dc/University_of_Portsmouth_Logo.png', '@port.ac.uk', '{\"buttoncolor\":\"#572985\",\"background\":\"#E5E7EB\"}');" +

                    "insert into society (universityId, societyName, societyDescription, societyPicture, maxSize, isPaid) values" +
                    "(1, 'IT Society', 'The IT Society is a place to hang with people with a passion for all things computing or those who are interested. Providing a platform for students to social network, learn and enhance their skills, while also facilitating those students who have a wilder side, wishing to relax and let their hair down in the evening and unplug from the internet of things.', 'https://memplus-dev.ams3.cdn.digitaloceanspaces.com/media/WKWR2fCewrxdqjov9WqyjT4N2YCu2hku2raKzrER.png', 0, false)," +
                    "(1, 'Basic Self Defence', 'Welcome to a new year at the Basic Self Defence Society!\nAt the Basic Self Defence Society, we offer a wide range of martial arts and self defence systems including: aikido, boxing, Brazilian jiu-jitsu, eskrima, judo and krav maga. Our free membership includes access to weekly sessions and workshops. The techniques you acquire in these sessions will not only improve your focus but develop your ability to confidently assess situations.\nBeyond physical defences, we find it important to teach the theory of self defence. This includes laws, situational awareness and vital/vulnerable points on the body. We also run weekly socials to give you the opportunity to meet new people from the university.\nIf you have any questions, please message our social media pages. We all look forward to meeting you!', 'https://memplus-dev.ams3.cdn.digitaloceanspaces.com/media/VUXTZYOjpHUgGAN7HJmVsK1r0e7H36yOmWGmx2sW.png', 40, false)," +
                    "(1, 'Dental', 'The Dental Society is about supporting students of the Dental Academy both academically and socially.\nThis includes rep sessions with products such as Philips Sonicare, Oral-B electric toothbrushes and Oralieve (including freebies) and talks with past students about preparing for practice.\nSocials such as bowling and a Christmas dinner are also organised for our members as well as a summer social, as a way of helping students make new friends and networking.\nMembership is free! So make sure to join up in order to take advantage of our dental events!', 'https://cdn.wildrocket.io/media/HkauYofYJDqICUEYJwKimW3iZ8650BDS7s2mNU0z.jpg', 0, false);" +

                    "insert into users (universityId, username, email, password, profilePicture, isAdministrator, settings) values" +
                    // Passwords are all 'password'
                    "(1, 'johndoe', 'johndoe@port.ac.uk', '$2y$10$wr1OF4PvzJX0nrfsJ6mumuriuI5MzNPdF.9nxzzElz2mldImt2n.O', 'https://api.dicebear.com/9.x/shapes/svg?seed=profile_johndoe.jpg', false, '{\"theme\":\"light\",\"notifications\":true}')," +
                    "(1, 'billytest', 'billytest@port.ac.uk', '$2y$10$D.6MBWU7ORf5Yt4ruW5/9OPs4RSFLyOYliqsepZ7vHhLbgIKkVH3.', 'https://api.dicebear.com/9.x/shapes/svg?seed=profile_billytest.jpg', false, '{\"theme\":\"light\",\"notifications\":true}');" +

//                    "insert into sessionToken (token, userid, expiry) values" +
//                    "('d4f7d8c6-6a8f-4c12-b914-7b6f20d1e8fb', 1, '2025-02-28 12:00:00')," +
//                    "('3b6f9bc8-8a65-4622-92f8-71b1d8faed3c', 2, '2025-02-25 09:00:00')," +
//                    "('ea2a6b1d-6a78-4a69-8a1b-e5c97b81d004', 3, '2025-03-02 15:00:00');" +

                    "insert into societyMember (userId, societyId, isManager) values" +
                    "(1, 1, true)," +
                    "(1, 2, false)," +
                    "(2, 2, false);"

//                    "insert into message (userId, societyId, messageContent, timestamp, isPinned) values" +
//                    "(1, 1, 'Welcome to the Computer Science Society!', '2025-02-15 10:00:00', true)," +
//                    "(2, 2, 'Join us for an exciting drama performance next week!', '2025-02-18 14:30:00', false)," +
//                    "(3, 3, 'We have an exciting new research opportunity available!', '2025-02-19 11:45:00', true);" +

//                    "insert into events (userId, startTimestamp, endTimestamp, createdTimestamp, location, name, description) values" +
//                    "(1, '2025-03-01 09:00:00', '2025-03-01 12:00:00', '2025-02-10 15:00:00', 'Room 101', 'Tech Talk on AI', 'Join us for a deep dive into Artificial Intelligence.')," +
//                    "(2, '2025-02-28 18:00:00', '2025-02-28 21:00:00', '2025-02-15 10:00:00', 'Auditorium', 'Drama Performance: Romeo and Juliet', 'Watch our talented actors perform the classic play, Romeo and Juliet.')," +
//                    "(3, '2025-03-05 13:00:00', '2025-03-05 16:00:00', '2025-02-20 09:00:00', 'Lab 202', 'Innovation Challenge', 'Participate in our innovation challenge and showcase your ideas.');" +

//                    "insert into societyEvent (societyId, eventId) values" +
//                    "(1, 1)," +
//                    "(2, 2)," +
//                    "(3, 3);"
                );
                statement.close();
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Resets the database by dropping all tables and reinitializing them
     */
    public static void resetDatabase() {
        try {
            createConnection(connection -> {
                Statement statement = connection.createStatement();
                statement.execute("""
                    drop table if exists societyEvent;
                    drop table if exists events;
                    drop table if exists message;
                    drop table if exists societyMember;
                    drop table if exists sessionToken;
                    drop table if exists users;
                    drop table if exists society;
                    drop table if exists university;
                """);
                statement.close();
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        initializeDatabase();
//        populateDatabase();
    }

}
