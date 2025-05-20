package database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class Tables {
    private static final Connection conn = DatabaseManager.getConnection();

    public static class DegreeProgram {
        private static final Logger log = LoggerFactory.getLogger(DegreeProgram.class);

        public int id;
        public String name;

        public DegreeProgram(String name) {
            this.name = name;
        }

        public void insert() {
            String sql = "INSERT INTO degree_program(name) VALUES (?)";
            try (PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                p.setString(1, name);
                p.executeUpdate();
                try (ResultSet rs = p.getGeneratedKeys()) {
                    if (rs.next()) this.id = rs.getInt(1);
                }
                log.info("DegreeProgram inserted, id={}", id);
            } catch (SQLException e) {
                log.error("Insert DegreeProgram failed", e);
                throw new RuntimeException(e);
            }
        }

        public static DegreeProgram selectById(int id) {
            String sql = "SELECT * FROM degree_program WHERE id = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setInt(1, id);
                try (ResultSet rs = p.executeQuery()) {
                    if (!rs.next())
                        return null;
                    DegreeProgram dp = new DegreeProgram(rs.getString("name"));
                    dp.id = id;
                    return dp;
                }
            } catch (SQLException e) {
                log.error("Select DegreeProgram failed", e);
                throw new RuntimeException(e);
            }
        }

        public static void delete(int id) {
            String sql = "DELETE FROM degree_program WHERE id = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setInt(1, id);
                p.executeUpdate();
                log.info("DegreeProgram deleted, id={}", id);
            } catch (SQLException e) {
                log.error("Delete DegreeProgram failed", e);
                throw new RuntimeException(e);
            }
        }

        public void updateName(String newName) {
            String sql = "UPDATE degree_program SET name = ? WHERE id = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setString(1, newName);
                p.setInt(2, id);
                p.executeUpdate();
                log.info("DegreeProgram updated, id={}", id);
                this.name = newName;
            } catch (SQLException e) {
                log.error("Update DegreeProgram failed", e);
                throw new RuntimeException(e);
            }
        }
    }

    //-------------------------------------------------------------------------

    public static class Student {
        private static final Logger log = LoggerFactory.getLogger(Student.class);

        public int id;
        public String name;
        public String email;
        public int programId;

        public Student(String name, String email, int programId) {
            this.name = name;
            this.email = email;
            this.programId = programId;
        }

        public void insert() {
            String sql = "INSERT INTO student(name, email, program_id) VALUES (?, ?, ?)";
            try (PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                p.setString(1, name);
                p.setString(2, email);
                p.setInt(3, programId);
                p.executeUpdate();
                try (ResultSet rs = p.getGeneratedKeys()) {
                    if (rs.next()) this.id = rs.getInt(1);
                }
                log.info("Student inserted, id={}", id);
            } catch (SQLException e) {
                log.error("Insert Student failed", e);
                throw new RuntimeException(e);
            }
        }

        public static Student selectById(int id) {
            String sql = "SELECT * FROM student WHERE id = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setInt(1, id);
                try (ResultSet rs = p.executeQuery()) {
                    if (!rs.next())
                        return null;
                    Student s = new Student(
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getInt("program_id")
                    );
                    s.id = id;
                    return s;
                }
            } catch (SQLException e) {
                log.error("Select Student failed", e);
                throw new RuntimeException(e);
            }
        }

        public static void delete(int id) {
            String sql = "DELETE FROM student WHERE id = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setInt(1, id);
                p.executeUpdate();
                log.info("Student deleted, id={}", id);
            } catch (SQLException e) {
                log.error("Delete Student failed", e);
                throw new RuntimeException(e);
            }
        }

        public void updateEmail(String newEmail) {
            String sql = "UPDATE student SET email = ? WHERE id = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setString(1, newEmail);
                p.setInt(2, id);
                p.executeUpdate();
                log.info("Student email updated, id={}", id);
                this.email = newEmail;
            } catch (SQLException e) {
                log.error("Update Student failed", e);
                throw new RuntimeException(e);
            }
        }
    }

    //-------------------------------------------------------------------------

    public static class Instructor {
        private static final Logger log = LoggerFactory.getLogger(Instructor.class);

        public int id;
        public String name;

        public Instructor(String name) {
            this.name = name;
        }

        public void insert() {
            String sql = "INSERT INTO instructor(name) VALUES (?)";
            try (PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                p.setString(1, name);
                p.executeUpdate();
                try (ResultSet rs = p.getGeneratedKeys()) {
                    if (rs.next()) this.id = rs.getInt(1);
                }
                log.info("Instructor inserted, id={}", id);
            } catch (SQLException e) {
                log.error("Insert Instructor failed", e);
                throw new RuntimeException(e);
            }
        }

        public static Instructor selectById(int id) {
            String sql = "SELECT * FROM instructor WHERE id = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setInt(1, id);
                try (ResultSet rs = p.executeQuery()) {
                    if (!rs.next())
                        return null;
                    Instructor ins = new Instructor(rs.getString("name"));
                    ins.id = id;
                    return ins;
                }
            } catch (SQLException e) {
                log.error("Select Instructor failed", e);
                throw new RuntimeException(e);
            }
        }

        public static void delete(int id) {
            String sql = "DELETE FROM instructor WHERE id = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setInt(1, id);
                p.executeUpdate();
                log.info("Instructor deleted, id={}", id);
            } catch (SQLException e) {
                log.error("Delete Instructor failed", e);
                throw new RuntimeException(e);
            }
        }

        public void updateName(String newName) {
            String sql = "UPDATE instructor SET name = ? WHERE id = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setString(1, newName);
                p.setInt(2, id);
                p.executeUpdate();
                log.info("Instructor updated, id={}", id);
                this.name = newName;
            } catch (SQLException e) {
                log.error("Update Instructor failed", e);
                throw new RuntimeException(e);
            }
        }
    }

    //-------------------------------------------------------------------------

    public static class Course {
        private static final Logger log = LoggerFactory.getLogger(Course.class);

        public int id;
        public String code;
        public String name;
        public String description;
        public int credits;
        public int programId;

        public Course(String code, String name, String description, int credits, int programId) {
            this.code = code;
            this.name = name;
            this.description = description;
            this.credits = credits;
            this.programId = programId;
        }

        public void insert() {
            String sql = "INSERT INTO course(code, name, description, credits, program_id) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                p.setString(1, code);
                p.setString(2, name);
                p.setString(3, description);
                p.setInt(4, credits);
                p.setInt(5, programId);
                p.executeUpdate();
                try (ResultSet rs = p.getGeneratedKeys()) {
                    if (rs.next()) this.id = rs.getInt(1);
                }
                log.info("Course inserted, id={}", id);
            } catch (SQLException e) {
                log.error("Insert Course failed", e);
                throw new RuntimeException(e);
            }
        }

        public static Course selectById(int id) {
            String sql = "SELECT * FROM course WHERE id = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setInt(1, id);
                try (ResultSet rs = p.executeQuery()) {
                    if (!rs.next())
                        return null;
                    Course c = new Course(
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getInt("credits"),
                            rs.getInt("program_id")
                    );
                    c.id = id;
                    return c;
                }
            } catch (SQLException e) {
                log.error("Select Course failed", e);
                throw new RuntimeException(e);
            }
        }

        public static void delete(int id) {
            String sql = "DELETE FROM course WHERE id = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setInt(1, id);
                p.executeUpdate();
                log.info("Course deleted, id={}", id);
            } catch (SQLException e) {
                log.error("Delete Course failed", e);
                throw new RuntimeException(e);
            }
        }

        public void updateName(String newName) {
            String sql = "UPDATE course SET name = ? WHERE id = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setString(1, newName);
                p.setInt(2, id);
                p.executeUpdate();
                log.info("Course name updated, id={}", id);
                this.name = newName;
            } catch (SQLException e) {
                log.error("Update Course failed", e);
                throw new RuntimeException(e);
            }
        }

        public void updateDescription(String newDescription) {
            String sql = "UPDATE course SET name = ? WHERE id = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setString(1, newDescription);
                p.setInt(2, id);
                p.executeUpdate();
                log.info("Course description updated, id={}", id);
                this.description = newDescription;
            } catch (SQLException e) {
                log.error("Update Course failed", e);
                throw new RuntimeException(e);
            }
        }

        public void updateCredits(int newCredits) {
            String sql = "UPDATE course SET name = ? WHERE id = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setInt(1, newCredits);
                p.setInt(2, id);
                p.executeUpdate();
                log.info("Course credit updated, id={}", id);
                this.credits = newCredits;
            } catch (SQLException e) {
                log.error("Update Course failed", e);
                throw new RuntimeException(e);
            }
        }

        public void updateProgramId(int newProgramId) {
            String sql = "UPDATE course SET name = ? WHERE id = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setInt(1, newProgramId);
                p.setInt(2, id);
                p.executeUpdate();
                log.info("Course program id updated, id={}", id);
                this.programId = newProgramId;
            } catch (SQLException e) {
                log.error("Update Course failed", e);
                throw new RuntimeException(e);
            }
        }
    }

    //-------------------------------------------------------------------------

    public static class Prerequisite {
        private static final Logger log = LoggerFactory.getLogger(Prerequisite.class);

        public int id;
        public int courseId;
        public int prerequisiteId;

        public Prerequisite(int courseId, int prerequisiteId) {
            this.courseId = courseId;
            this.prerequisiteId = prerequisiteId;
        }

        public void insert() {
            String sql = "INSERT INTO prerequisite(course_id, prerequisite_id) VALUES (?, ?)";
            try (PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                p.setInt(1, courseId);
                p.setInt(2, prerequisiteId);
                p.executeUpdate();
                try (ResultSet rs = p.getGeneratedKeys()) {
                    if (rs.next()) this.id = rs.getInt(1);
                }
                log.info("Prerequisite inserted, id={}", id);
            } catch (SQLException e) {
                log.error("Insert Prerequisite failed", e);
                throw new RuntimeException(e);
            }
        }

        public static void delete(int id) {
            String sql = "DELETE FROM prerequisite WHERE id = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setInt(1, id);
                p.executeUpdate();
                log.info("Prerequisite deleted, id={}", id);
            } catch (SQLException e) {
                log.error("Delete Prerequisite failed", e);
                throw new RuntimeException(e);
            }
        }

        public static Prerequisite selectById(int id) {
            String sql = "SELECT * FROM prerequisite WHERE id = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setInt(1, id);
                try (ResultSet rs = p.executeQuery()) {
                    if (!rs.next())
                        return null;
                    Prerequisite pr = new Prerequisite(
                            rs.getInt("course_id"),
                            rs.getInt("prerequisite_id")
                    );
                    pr.id = id;
                    return pr;
                }
            } catch (SQLException e) {
                log.error("Select Prerequisite failed", e);
                throw new RuntimeException(e);
            }
        }

        public void update(int newCourseId, int newPrerequisiteId) {
            String sql = "UPDATE prerequisite SET course_id = ?, prerequisite_id = ? WHERE id = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setInt(1, newCourseId);
                p.setInt(2, newPrerequisiteId);
                p.setInt(3, id);
                p.executeUpdate();
                this.courseId = newCourseId;
                this.prerequisiteId = newPrerequisiteId;
                log.info("Prerequisite updated, id={}", id);
            } catch (SQLException e) {
                log.error("Update Prerequisite failed", e);
                throw new RuntimeException(e);
            }
        }
    }

    //-------------------------------------------------------------------------

    public static class Schedule {
        private static final Logger log = LoggerFactory.getLogger(Schedule.class);

        public int id;
        public int courseId;
        public int instructorId;
        public Timestamp startTime;
        public Timestamp endTime;
        public String location;
        public String semester;

        public Schedule(int courseId, int instructorId, Timestamp startTime, Timestamp endTime, String location, String semester) {
            this.courseId = courseId;
            this.instructorId = instructorId;
            this.startTime = startTime;
            this.endTime = endTime;
            this.location = location;
            this.semester = semester;
        }

        public void insert() {
            String sql = "INSERT INTO schedule(course_id, instructor_id, start_time, end_time, location, semester) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                p.setInt(1, courseId);
                p.setInt(2, instructorId);
                p.setTimestamp(3, startTime);
                p.setTimestamp(4, endTime);
                p.setString(5, location);
                p.setString(6, semester);
                p.executeUpdate();
                try (ResultSet rs = p.getGeneratedKeys()) {
                    if (rs.next()) this.id = rs.getInt(1);
                }
                log.info("Schedule inserted, id={}", id);
            } catch (SQLException e) {
                log.error("Insert Schedule failed", e);
                throw new RuntimeException(e);
            }
        }

        public static void delete(int id) {
            String sql = "DELETE FROM schedule WHERE id = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setInt(1, id);
                p.executeUpdate();
                log.info("Schedule deleted, id={}", id);
            } catch (SQLException e) {
                log.error("Delete Schedule failed", e);
                throw new RuntimeException(e);
            }
        }

        public void updateLocation(String newLocation) {
            String sql = "UPDATE schedule SET name = ? WHERE id = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setString(1, newLocation);
                p.setInt(2, id);
                p.executeUpdate();
                log.info("Schedule location updated, id={}", id);
                this.location = newLocation;
            } catch (SQLException e) {
                log.error("Update Schedule failed", e);
                throw new RuntimeException(e);
            }
        }

        public void updateSemester(String newSemester) {
            String sql = "UPDATE schedule SET name = ? WHERE id = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setString(1, newSemester);
                p.setInt(2, id);
                p.executeUpdate();
                log.info("Schedule semester updated, id={}", id);
                this.semester = newSemester;
            } catch (SQLException e) {
                log.error("Update Schedule failed", e);
                throw new RuntimeException(e);
            }
        }

        public static Schedule selectById(int id) {
            String sql = "SELECT * FROM schedule WHERE id = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setInt(1, id);
                try (ResultSet rs = p.executeQuery()) {
                    if (!rs.next())
                        return null;
                    Schedule s = new Schedule(
                            rs.getInt("course_id"),
                            rs.getInt("instructor_id"),
                            rs.getTimestamp("start_time"),
                            rs.getTimestamp("end_time"),
                            rs.getString("location"),
                            rs.getString("semester")
                    );
                    s.id = rs.getInt("id");
                    return s;
                }
            } catch (SQLException e) {
                log.error("Select Schedule failed", e);
                throw new RuntimeException(e);
            }
        }
    }

    //-------------------------------------------------------------------------

    public static class Enrollment {
        private static final Logger log = LoggerFactory.getLogger(Enrollment.class);

        public int id;
        public int studentId;
        public int scheduleId;
        public String status;

        public Enrollment(int studentId, int scheduleId, String status) {
            this.studentId = studentId;
            this.scheduleId = scheduleId;
            this.status = status;
        }

        public void insert() {
            String sql = "INSERT INTO enrollment(student_id, schedule_id, status) VALUES (?, ?, ?)";
            try (PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                p.setInt(1, studentId);
                p.setInt(2, scheduleId);
                p.setString(3, status);
                p.executeUpdate();
                try (ResultSet rs = p.getGeneratedKeys()) {
                    if (rs.next()) this.id = rs.getInt(1);
                }
                log.info("Enrollment inserted, id={}", id);
            } catch (SQLException e) {
                log.error("Insert Enrollment failed", e);
                throw new RuntimeException(e);
            }
        }

        public static void delete(int id) {
            String sql = "DELETE FROM enrollment WHERE id = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setInt(1, id);
                p.executeUpdate();
                log.info("Enrollment deleted, id={}", id);
            } catch (SQLException e) {
                log.error("Delete Enrollment failed", e);
                throw new RuntimeException(e);
            }
        }

        public void updateStatus(String newStatus) {
            String sql = "UPDATE enrollment SET status = ? WHERE id = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setString(1, newStatus);
                p.setInt(2, id);
                p.executeUpdate();
                log.info("Enrollment status updated, id={}", id);
                this.status = newStatus;
            } catch (SQLException e) {
                log.error("Update Enrollment failed", e);
                throw new RuntimeException(e);
            }
        }

        public static Enrollment selectById(int id) {
            String sql = "SELECT * FROM enrollment WHERE id = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setInt(1, id);
                try (ResultSet rs = p.executeQuery()) {
                    if (!rs.next())
                        return null;
                    Enrollment e = new Enrollment(
                            rs.getInt("student_id"),
                            rs.getInt("schedule_id"),
                            rs.getString("status")
                    );
                    e.id = rs.getInt("id");
                    return e;
                }
            } catch (SQLException e) {
                log.error("Select Enrollment failed", e);
                throw new RuntimeException(e);
            }
        }
    }
}