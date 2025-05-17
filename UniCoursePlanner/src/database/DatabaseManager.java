package org.postgresql.connection;

import java.io.InputStream;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;
import java.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseManager {
    private static  String JDBC_URL;
    private static String USERNAME;
    private static String PASSWORD;
    private static Connection conn;
    private static Logger log = LoggerFactory.getLogger(org.postgresql.connection.DatabaseManager.class);

    public static class DatabaseException extends RuntimeException {
        public DatabaseException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public DatabaseManager(){
        try {
            Properties props = new Properties();
            InputStream input = new FileInputStream("src/main/java/org/postgresql/connection/config.properties");
            props.load(input);

            JDBC_URL = props.getProperty("db.url");
            USERNAME = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");
        } catch (IOException e) {
            log.error("Failed to load config.properties", e);
            throw new RuntimeException("Failed to load config.properties", e);
        }
        conn = dbConnection();
        if (conn == null){
            log.error("Database connection initialization returned null");
            throw new IllegalStateException("Database connection failed");
        }
    }

    public void connClose() {
        try {
            conn.close();
            log.info("Connection closed");
        } catch (SQLException e) {
            log.error("Error closing database connection", e);
            throw new org.postgresql.connection.DatabaseManager.DatabaseException("Error closing connection", e);
        }
    }

    public static Connection dbConnection() {
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            log.info("Database connection established");
            return connection;
        } catch (SQLException e) {
            log.error("Connection could not be established", e);
            throw new org.postgresql.connection.DatabaseManager.DatabaseException("Unable to establish connection", e);
        }
    }

    public void executeUpdate(String sql, Object... params) {
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            int affected = pstmt.executeUpdate();
            log.info("DML executed, rows affected = {}", affected);
        } catch (SQLException e) {
            log.error("DML failed: {}", sql, e);
            throw new org.postgresql.connection.DatabaseManager.DatabaseException("Error executing update: " + sql, e);
        }
    }

    public void insertCourse(String name, String description) {
        String sql = "INSERT INTO course(name, description) VALUES (?, ?)";
        executeUpdate(sql, name, description);
    }

    public void insertInstructor(String name) {
        String sql = "INSERT INTO instructor(name) VALUES (?)";
        executeUpdate(sql, name);
    }

    public void insertSchedule(int courseId, int instructorId, String start, String end, String location) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm");
        LocalDateTime ldtStart = LocalDateTime.parse(start, fmt);
        LocalDateTime ldtEnd   = LocalDateTime.parse(end,   fmt);

        Timestamp tsStart = Timestamp.valueOf(ldtStart);
        Timestamp tsEnd   = Timestamp.valueOf(ldtEnd);

        String sql = "INSERT INTO schedule(course_id, instructor_id, start_time, end_time, location) VALUES (?, ?, ?, ?, ?)";
        executeUpdate(sql, courseId, instructorId, tsStart, tsEnd, location);
    }

    public void deleteCourse(int id) {
        executeUpdate("DELETE FROM course WHERE id = ?", id);
    }

    public void deleteInstructor(int id) {
        executeUpdate("DELETE FROM instructor WHERE id = ?", id);
    }

    public void deleteSchedule(int id) {
        executeUpdate("DELETE FROM schedule WHERE id = ?", id);
    }

    public void updateCourseName(int id, String newName) {
        executeUpdate("UPDATE course SET name = ? WHERE id = ?", newName, id);
    }

    public void updateCourseDescription(int id, String newDesc) {
        executeUpdate("UPDATE course SET description = ? WHERE id = ?", newDesc, id);
    }

    public void updateInstructorName(int id, String newName) {
        executeUpdate("UPDATE instructor SET name = ? WHERE id = ?", newName, id);
    }

    public void updateScheduleLocation(int id, String newLocation) {
        executeUpdate("UPDATE schedule SET location = ? WHERE id = ?", newLocation, id);
    }

    public void selection(String tableName, int id) {
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int columns = meta.getColumnCount();

                boolean flag = false;
                while (rs.next()) {
                    flag = true;
                    log.info("Row from {} (id={}):", tableName, id);
                    for (int i = 1; i <= columns; i++) {
                        String name  = rs.getMetaData().getColumnName(i);
                        Object value = rs.getString(i);
                        System.out.println(name + " = " + value);
                        log.info("  {} = {}", name, value); //not sure
                    }
                    System.out.println();
                }

                if (!flag) {
                    log.warn("No rows found in {} with id={}", tableName, id);
                }
            }
        } catch (SQLException e) {
            log.error("SELECT failed for {} id={} SQL={} ", tableName, id, sql, e);
            throw new org.postgresql.connection.DatabaseManager.DatabaseException("Error executing query: " + sql, e);
        }
    }

    public void selectCourse(int id) {
        selection("course", id);
    }

    public void selectInstructor(int id) {
        selection("instructor", id);
    }

    public void selectSchedule(int id) {
        selection("schedule", id);
    }
}