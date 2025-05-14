package uk.ac.port.setap.team6c.database;

import lombok.Getter;
import org.apache.commons.dbcp2.BasicDataSource;
import org.jetbrains.annotations.NotNull;
import uk.ac.port.setap.team6c.Main;

import java.sql.*;
import java.util.Optional;

/**
 * This class manages all connections with the database and is responsible for creating the database on startup
 */
public class DatabaseManager {

    @Getter
    private static final BasicDataSource source;

    // When this class is loaded, set up the connection with the database
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
    static Optional<ResultSet> populateAndExecute(@NotNull Connection connection, String query,
                                                  @NotNull Object @NotNull ... params) throws SQLException {
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
    public static void populateDatabase() {
        try {
            createConnection(connection -> {
                Statement statement = connection.createStatement();
                statement.execute(
                    "insert into university (universityName, universityPicture, emailDomain, theming) values" +
                    "('University of Portsmouth', 'https://upload.wikimedia.org/wikipedia/commons/d/dc/University_of_Portsmouth_Logo.png', '@port.ac.uk', '{\"primarycolor\":\"#572985\"}');" +

                    "insert into society (universityId, societyName, societyDescription, societyPicture) values" +
                    "(1, 'IT Society', 'The IT Society is a place to hang with people with a passion for all things computing or those who are interested. Providing a platform for students to social network, learn and enhance their skills, while also facilitating those students who have a wilder side, wishing to relax and let their hair down in the evening and unplug from the internet of things.', 'https://memplus-dev.ams3.cdn.digitaloceanspaces.com/media/WKWR2fCewrxdqjov9WqyjT4N2YCu2hku2raKzrER.png')," +
                    "(1, 'Basic Self Defence', 'Welcome to a new year at the Basic Self Defence Society!\nAt the Basic Self Defence Society, we offer a wide range of martial arts and self defence systems including: aikido, boxing, Brazilian jiu-jitsu, eskrima, judo and krav maga. Our free membership includes access to weekly sessions and workshops. The techniques you acquire in these sessions will not only improve your focus but develop your ability to confidently assess situations.\nBeyond physical defences, we find it important to teach the theory of self defence. This includes laws, situational awareness and vital/vulnerable points on the body. We also run weekly socials to give you the opportunity to meet new people from the university.\nIf you have any questions, please message our social media pages. We all look forward to meeting you!', 'https://memplus-dev.ams3.cdn.digitaloceanspaces.com/media/VUXTZYOjpHUgGAN7HJmVsK1r0e7H36yOmWGmx2sW.png')," +
                    "(1, 'Dental', 'The Dental Society is about supporting students of the Dental Academy both academically and socially.\nThis includes rep sessions with products such as Philips Sonicare, Oral-B electric toothbrushes and Oralieve (including freebies) and talks with past students about preparing for practice.\nSocials such as bowling and a Christmas dinner are also organised for our members as well as a summer social, as a way of helping students make new friends and networking.\nMembership is free! So make sure to join up in order to take advantage of our dental events!', 'https://cdn.wildrocket.io/media/HkauYofYJDqICUEYJwKimW3iZ8650BDS7s2mNU0z.jpg');" +

                    // Passwords are all 'password'
                    "insert into users (universityId, username, email, password, profilePicture) values" +
                    "(1, 'johndoe', 'johndoe@port.ac.uk', '$2y$10$wr1OF4PvzJX0nrfsJ6mumuriuI5MzNPdF.9nxzzElz2mldImt2n.O', 'https://api.dicebear.com/9.x/shapes/svg?seed=johndoe')," +
                    "(1, 'billytest', 'billytest@port.ac.uk', '$2a$12$COAC0Iko4ey9tb3xAaAVI.cF1vt67WzOa.lmEBPxkLXzBOJetRQxS', 'https://api.dicebear.com/9.x/shapes/svg?seed=billytest');" +

                    "insert into societyMember (userId, societyId) values" +
                    "(1, 1)," +
                    "(1, 2)," +
                    "(2, 2);"
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
    }

}
