package database;

import database.Tables.*;

public class Test {
    public static void main (String[] args){
        //opens connection
        DatabaseManager db = new DatabaseManager();

        // Create a degree program
        DegreeProgram dp = new DegreeProgram("Computer Science");
        dp.insert();

        // Create a student
        Student st = new Student("AAA", "AAA@example.com", dp.id);
        st.insert();

        //Create an instructor
        Instructor ins = new Instructor("BBB");
        ins.insert();

        // Create a course
        Course cr = new Course("CS626", "Intro to OOP", "Basics of OOP", 3, dp.id);
        cr.insert();

        // Define a prerequisite for that course
        Prerequisite pre = new Prerequisite(cr.id, cr.id);
        pre.insert();

        // Schedule the course
        Schedule sch = new Schedule(cr.id, ins.id, java.sql.Timestamp.valueOf("2025-09-01 09:00:00"), java.sql.Timestamp.valueOf("2025-09-01 10:20:00"), "313W", "Fall2025");
        sch.insert();

        // Enroll the student in that schedule
        Enrollment enr = new Enrollment(st.id, sch.id, "enrolled");
        enr.insert();

        // Read back and update some entities
        Course loadedCourse = Course.selectById(cr.id);
        loadedCourse.updateName("CS626 – Introduction to Object-Oriented Programming");

        // Delete all
        Enrollment.delete(enr.id);
        Schedule.delete(sch.id);
        Prerequisite.delete(pre.id);
        Course.delete(cr.id);
        Instructor.delete(ins.id);
        Student.delete(st.id);
        DegreeProgram.delete(dp.id);

        // 11) Close connection
        db.connClose();
    }
}
