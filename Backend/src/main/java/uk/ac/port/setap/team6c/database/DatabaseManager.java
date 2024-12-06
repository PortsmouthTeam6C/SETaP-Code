package uk.ac.port.setap.team6c.database;

import org.apache.commons.dbcp2.BasicDataSource;
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
     * Creates and automatically closes a database connection and statement
     * @param function The function to run once the statement is created
     * @throws SQLException A SQLException will be returned if something goes wrong with the database query
     */
    private static void runSQL(SQLConsumer function) throws SQLException {
        Connection connection = source.getConnection();
        Statement statement = connection.createStatement();
        function.accept(statement);
        statement.close();
        connection.close();
    }

    /**
     * Used for inline running of SQL statements by the {@link DatabaseManager.SQLConsumer} method
     */
    @FunctionalInterface
    private interface SQLConsumer {
        void accept(Statement statement) throws SQLException;
    }

}
