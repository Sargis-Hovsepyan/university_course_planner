package org.postgreSQL_connection;

import java.sql.*;

public class DatabaseManager {
    private static final String JDBC_URL  = "jdbc:postgresql://localhost:5432/university";
    private static final String USERNAME  = "postgres";
    private static final String PASSWORD  = "ansergart";
    private Connection conn;

    public DatabaseManager(){
        conn = dbConnection();
    }

    public void connClose(){
        try {
            conn.close();
        } catch (SQLException ignored) {}
    }

    public static Connection dbConnection() {
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            System.out.println("The connection was successful!");
            return connection;
        } catch (SQLException e) {
            System.out.println("Connection could not be established!");
            e.printStackTrace();
            return null;
        }
    }

    public void insertCourse(String name, String description){
        String query = "INSERT INTO course(name, description) VALUES ('" + name + "', '" + description + "')";
        insertion(query);
    }

    public void insertInstructor(String name){
        String query = "INSERT INTO instructor(name) VALUES ('" + name + "')";
        insertion(query);
    }

    public void insertSchedule(int courseId, int instructorId, String startTime, String endTime, String location){
        String query = "INSERT INTO schedule(course_id, instructor_id, start_time, end_time, location) VALUES (" + courseId + ", " + instructorId + ", '" + startTime + "', '" + endTime + "', '" + location + "')";
        insertion(query);
    }

    public void insertion(String query){
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("The insertion was successful");
            } else {
                System.out.println("No rows inserted");
            }
        } catch (SQLException e) {
            System.err.println("Insert failed!");
            e.printStackTrace();
        }
    }

    public void deleteCourse(int id){
        deletion(id, "course");
    }

    public void deleteInstructor(int id){
        deletion(id, "instructor");
    }

    public void deleteSchedule(int id){
        deletion(id, "schedule");
    }

    public void deletion(int id, String from){
        String query = "DELETE FROM "+ from +" WHERE id = " + id ;
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("The deletion was successful");
            } else {
                System.out.println("No rows deleted");
            }
        }catch (SQLException e) {
            System.err.println("Delete failed!");
            e.printStackTrace();
        }
    }

    public void selectInstructor(int id) {
        selection(id, "instructor");
        System.out.println();
    }

    public void selectCourse(int id) {
        selection(id, "course");
        System.out.println();
    }

    public void selectSchedule(int id) {
        selection(id, "schedule");
        System.out.println();
    }

    //selection only by id
    public void selection (int id, String from){
        String query = "SELECT * FROM "+ from +" WHERE id = " + id ;
        try(PreparedStatement pstmt = conn.prepareStatement(query)){
            ResultSet result = pstmt.executeQuery();

            if (!result.next()) {
                System.out.println("No rows found in " + from + " with id = " + id);
                return;
            }

            ResultSetMetaData meta = pstmt.getMetaData();
            for (int i = 1; i <= meta.getColumnCount(); i++){
                String name  = result.getMetaData().getColumnName(i);
                String value = result.getString(i);
                System.out.println(name + " = " + value);
            }

        }catch (SQLException e) {
            System.err.println("SELECT failed for " + from + " id=" + id);
            e.printStackTrace();
        }
    }

    public void updateCourseName(int id, String newName) {
        update("course", id, "name", newName);
    }
    public void updateCourseDescription(int id, String newDesc) {
        update("course", id, "description", newDesc);
    }

    public void updateInstructorName(int id, String newName) {
        update("instructor", id, "name", newName);
    }

    public void updateScheduleLocation(int id, String newLocation) {
        update("schedule", id, "location", newLocation);
    }

    public void update(String tableName, int id, String columnName, String newValue){
        String query = "UPDATE " + tableName +
                " SET "   + columnName + " = '" + newValue + "'" +
                " WHERE id = " + id;

        try(PreparedStatement pstmt = conn.prepareStatement(query)){
            int result = pstmt.executeUpdate();
            if (result > 0){
                System.out.println("Update was successful in " + tableName + ", id affected: " + id);
            }else {
                System.out.println("No rows updated in " + tableName + " for id=" + id);
            }
        }catch (SQLException e) {
            System.err.println("Update failed for " + tableName + " id =" + id);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DatabaseManager db = new DatabaseManager();

        // insert test
        //db.insertInstructor("AAA");
        //db.insertCourse("Java Programming", "Fundamentals of Java, OOP, JDBC");
        //db.insertSchedule(7, 5, "2025-09-01 9:00", "2025-09-01 10:20", "W 313");

        // delete insert
        //db.deleteCourse(6);
        //db.deleteInstructor(4);
        //db.deleteSchedule(1);

        // select test
        db.selectCourse(7);
        db.selectInstructor(5);
        db.selectSchedule(6);

        //update test
        db.updateCourseName(7, "Music");
        db.updateCourseDescription(7, "It is music");
        db.updateInstructorName(5, "BBB");
        db.updateScheduleLocation(6, "413W");

        db.selectCourse(7);
        db.selectInstructor(5);
        db.selectSchedule(6);

        db.connClose();
    }
}