package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import service.Environment;

public class DatabaseManager {
    private static final String JDBC_URL = Environment.get("DB_URL");
    private static final String USERNAME = Environment.get("DB_USER");
    private static final String PASSWORD = Environment.get("DB_PASSWORD");
    private static final Logger log = LoggerFactory.getLogger(DatabaseManager.class);
    private static Connection conn;

    public static class DatabaseException extends RuntimeException {
        public DatabaseException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public DatabaseManager() {
        conn = dbConnection();
        if (conn == null) {
            log.error("Database connection initialization returned null");
            throw new IllegalStateException("Database connection failed");
        }
    }

    public static Connection getConnection() {
        return conn;
    }

    public static Connection dbConnection() {
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            log.info("Database connection established");
            return connection;
        } catch (SQLException e) {
            log.error("Connection could not be established", e);
            throw new DatabaseException("Unable to establish connection", e);
        }
    }

    public void connClose() {
        try {
            conn.close();
            log.info("Connection closed");
        } catch (SQLException e) {
            log.error("Error closing database connection", e);
            throw new DatabaseManager.DatabaseException("Error closing connection", e);
        }
    }
}