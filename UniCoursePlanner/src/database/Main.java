package database;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        StartUp startup = StartUp.getInstance();

        Tables.setConnection(conn);

        /* Inserting Data */


        String projectRoot = System.getProperty("user.dir");
        Path parentDir = Paths.get(projectRoot).getParent();

        Path pathPrograms = parentDir.resolve("university_course_planner/data/university_degree_programs.csv");
        startup.readDegreePrograms(pathPrograms.toString());


        Path pathInstructors = parentDir.resolve("university_course_planner/data/university_instructors.csv");
        startup.readInstructors(pathInstructors.toString());

        Path pathCourses = parentDir.resolve("university_course_planner/data/university_courses.csv");
        startup.readCourses(pathCourses.toString());

        Path pathSchedules = parentDir.resolve("university_course_planner/data/university_schedules.csv");
        startup.readSchedules(pathSchedules.toString());

        conn.close();
    }
}
