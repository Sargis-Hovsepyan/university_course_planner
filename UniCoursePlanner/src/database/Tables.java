package database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Tables {
    private static Connection conn = null;

    public static void setConnection(Connection conn) {
        Tables.conn = conn;
    }

    public static class DegreeProgram {
        private static final Logger log = LoggerFactory.getLogger(DegreeProgram.class);

        public String code;   // changed from int id to String code (PK in DB)
        public String name;
        public String type;   // added type field, since program table has it

        // Constructor for creating a new DegreeProgram before insert (code might be generated or assigned)
        public DegreeProgram(String code, String name, String type) {
            this.code = code;
            this.name = name;
            this.type = type;
        }

        // Insert method - program.code is PK, so it must be set before insert (not auto-generated)
        public void insert() {
            String sql = "INSERT INTO program(code, name, type) VALUES (?, ?, ?)";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setString(1, code);
                p.setString(2, name);
                p.setString(3, type);
                p.executeUpdate();
                log.info("DegreeProgram inserted, code={}", code);
            } catch (SQLException e) {
                log.error("Insert DegreeProgram failed", e);
                throw new RuntimeException(e);
            }
        }

        // Select program by code (primary key)
        public static DegreeProgram selectByCode(String code) {
            String sql = "SELECT * FROM program WHERE code = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setString(1, code);
                try (ResultSet rs = p.executeQuery()) {
                    if (!rs.next())
                        return null;
                    DegreeProgram dp = new DegreeProgram(
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getString("type")
                    );
                    return dp;
                }
            } catch (SQLException e) {
                log.error("Select DegreeProgram failed", e);
                throw new RuntimeException(e);
            }
        }

        // Delete by code
        public static void delete(String code) {
            String sql = "DELETE FROM program WHERE code = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setString(1, code);
                p.executeUpdate();
                log.info("DegreeProgram deleted, code={}", code);
            } catch (SQLException e) {
                log.error("Delete DegreeProgram failed", e);
                throw new RuntimeException(e);
            }
        }

        // Update name and type by code (primary key)
        public void update(String newName, String newType) {
            String sql = "UPDATE program SET name = ?, type = ? WHERE code = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setString(1, newName);
                p.setString(2, newType);
                p.setString(3, code);
                p.executeUpdate();
                log.info("DegreeProgram updated, code={}", code);
                this.name = newName;
                this.type = newType;
            } catch (SQLException e) {
                log.error("Update DegreeProgram failed", e);
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
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Instructor name cannot be null or empty");
            }
            this.name = name;
        }

        // Optional constructor to create an Instructor with id (e.g., from DB)
        public Instructor(int id, String name) {
            this(name);
            this.id = id;
        }

        public void insert() {
            String sql = "INSERT INTO instructor(name) VALUES (?)";
            try (PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                p.setString(1, name);
                p.executeUpdate();
                try (ResultSet rs = p.getGeneratedKeys()) {
                    if (rs.next()) {
                        this.id = rs.getInt(1);
                    }
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
                    if (!rs.next()) return null;
                    return new Instructor(id, rs.getString("name"));
                }
            } catch (SQLException e) {
                log.error("Select Instructor failed", e);
                throw new RuntimeException(e);
            }
        }

        // Optional: Select by name if useful
        public static Instructor selectByName(String name) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Name cannot be null or empty");
            }
            String sql = "SELECT * FROM instructor WHERE name = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setString(1, name);
                try (ResultSet rs = p.executeQuery()) {
                    if (!rs.next()) return null;
                    return new Instructor(rs.getInt("id"), name);
                }
            } catch (SQLException e) {
                log.error("Select Instructor by name failed", e);
                throw new RuntimeException(e);
            }
        }

        public static void delete(int id) {
            String sql = "DELETE FROM instructor WHERE id = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setInt(1, id);
                int rows = p.executeUpdate();
                if (rows > 0) {
                    log.info("Instructor deleted, id={}", id);
                } else {
                    log.warn("No Instructor found to delete with id={}", id);
                }
            } catch (SQLException e) {
                log.error("Delete Instructor failed", e);
                throw new RuntimeException(e);
            }
        }

        public void updateName(String newName) {
            if (newName == null || newName.isBlank()) {
                throw new IllegalArgumentException("New name cannot be null or empty");
            }
            String sql = "UPDATE instructor SET name = ? WHERE id = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setString(1, newName);
                p.setInt(2, id);
                int rows = p.executeUpdate();
                if (rows > 0) {
                    log.info("Instructor updated, id={}", id);
                    this.name = newName;
                } else {
                    log.warn("No Instructor found to update with id={}", id);
                }
            } catch (SQLException e) {
                log.error("Update Instructor failed", e);
                throw new RuntimeException(e);
            }
        }
    }

    //-------------------------------------------------------------------------

    public static class Course {
        private static final Logger log = LoggerFactory.getLogger(Course.class);

        // Fields match your DB schema
        public String code;                 // PRIMARY KEY
        public String programCode;          // FK to program(code)
        public String title;
        public String description;
        public Double credits;              // Use Double to match NUMERIC(3,1)
        public String prerequisiteCode;    // FK to course(code), nullable

        public Course(String code, String programCode, String title, String description, Double credits, String prerequisiteCode) {
            this.code = code;
            this.programCode = programCode;
            this.title = title;
            this.description = description;
            this.credits = credits;
            this.prerequisiteCode = prerequisiteCode;
        }

        public void insert() {
            String sql = "INSERT INTO course(code, program_code, title, description, credits, prerequisite_code) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setString(1, code);
                p.setString(2, programCode);
                p.setString(3, title);
                p.setString(4, description);
                if (credits == null) {
                    p.setNull(5, Types.NUMERIC);
                } else {
                    p.setDouble(5, credits);
                }
                if (prerequisiteCode == null) {
                    p.setNull(6, Types.VARCHAR);
                } else {
                    p.setString(6, prerequisiteCode);
                }
                p.executeUpdate();
                log.info("Course inserted, code={}", code);
            } catch (SQLException e) {
                log.error("Insert Course failed", e);
                throw new RuntimeException(e);
            }
        }

        public static Course selectByCode(String code) {
            String sql = "SELECT * FROM course WHERE code = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setString(1, code);
                try (ResultSet rs = p.executeQuery()) {
                    if (!rs.next()) return null;
                    return new Course(
                            rs.getString("code"),
                            rs.getString("program_code"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getObject("credits") != null ? rs.getDouble("credits") : null,
                            rs.getString("prerequisite_code")
                    );
                }
            } catch (SQLException e) {
                log.error("Select Course failed", e);
                throw new RuntimeException(e);
            }
        }

        public static void delete(String code) {
            String sql = "DELETE FROM course WHERE code = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setString(1, code);
                int rows = p.executeUpdate();
                if (rows > 0) {
                    log.info("Course deleted, code={}", code);
                } else {
                    log.warn("No course found to delete with code={}", code);
                }
            } catch (SQLException e) {
                log.error("Delete Course failed", e);
                throw new RuntimeException(e);
            }
        }

        /**
         * Update the course fields dynamically based on the given map of column->value.
         * Example keys: "title", "description", "credits", "program_code", "prerequisite_code"
         *
         * @param updates Map of column names to new values
         */
        public void update(Map<String, Object> updates) {
            if (updates == null || updates.isEmpty()) {
                log.warn("No fields provided to update for course code={}", code);
                return;
            }

            StringBuilder sql = new StringBuilder("UPDATE course SET ");
            int count = 0;
            for (String field : updates.keySet()) {
                if (count > 0) sql.append(", ");
                sql.append(field).append(" = ?");
                count++;
            }
            sql.append(" WHERE code = ?");

            try (PreparedStatement p = conn.prepareStatement(sql.toString())) {
                int index = 1;
                for (String field : updates.keySet()) {
                    Object value = updates.get(field);
                    if (value == null) {
                        // Set SQL NULL based on expected column type, simplified as VARCHAR or NUMERIC
                        if ("credits".equals(field)) {
                            p.setNull(index, Types.NUMERIC);
                        } else {
                            p.setNull(index, Types.VARCHAR);
                        }
                    } else if (value instanceof String) {
                        p.setString(index, (String) value);
                    } else if (value instanceof Integer) {
                        p.setInt(index, (Integer) value);
                    } else if (value instanceof Double) {
                        p.setDouble(index, (Double) value);
                    } else {
                        p.setObject(index, value);
                    }
                    index++;
                }
                // WHERE clause parameter (code)
                p.setString(index, code);

                int rows = p.executeUpdate();
                if (rows > 0) {
                    log.info("Updated course code={} with fields {}", code, updates.keySet());

                    // Update local fields to keep object consistent
                    for (Map.Entry<String, Object> entry : updates.entrySet()) {
                        String field = entry.getKey();
                        Object val = entry.getValue();

                        switch (field) {
                            case "title":
                                this.title = (String) val;
                                break;
                            case "description":
                                this.description = (String) val;
                                break;
                            case "credits":
                                if (val == null) this.credits = null;
                                else if (val instanceof Double) this.credits = (Double) val;
                                else this.credits = Double.parseDouble(val.toString());
                                break;
                            case "program_code":
                                this.programCode = (String) val;
                                break;
                            case "prerequisite_code":
                                this.prerequisiteCode = (String) val;
                                break;
                        }
                    }
                } else {
                    log.warn("No course found with code={} to update", code);
                }
            } catch (SQLException e) {
                log.error("Update Course failed for code=" + code, e);
                throw new RuntimeException(e);
            }
        }
    }

    //-------------------------------------------------------------------------

    public static class Schedule {
        private static final Logger log = LoggerFactory.getLogger(Schedule.class);

        public int id;
        public String courseCode;
        public String courseName;
        public String section;
        public String session;
        public double credits;
        public String campus;
        public String instructor;
        public String times;    // e.g., "MON 5:30pm-7:20pm, WED 6:30pm-7:20pm"
        public String location;
        public String semester; // e.g., 202324/fall

        public Schedule(String courseCode, String courseName, String section, String session, double credits,
                        String campus, String instructor, String times, String location, String semester) {
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.section = section;
            this.session = session;
            this.credits = credits;
            this.campus = campus;
            this.instructor = instructor;
            this.times = times;
            this.location = location;
            this.semester = semester;
        }

        public void insert() {
            String sql = "INSERT INTO schedule(course_code, course_name, section, session, credits, campus, instructor, times, location, semester) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                p.setString(1, courseCode);
                p.setString(2, courseName);
                p.setString(3, section);
                p.setString(4, session);
                p.setDouble(5, credits);
                p.setString(6, campus);
                p.setString(7, instructor);
                p.setString(8, times);
                p.setString(9, location);
                p.setString(10, semester);

                p.executeUpdate();
                try (ResultSet rs = p.getGeneratedKeys()) {
                    if (rs.next()) {
                        this.id = rs.getInt(1);
                    }
                }
                log.info("Schedule inserted, id={}", id);
            } catch (SQLException e) {
                log.error("Insert Schedule failed", e);
                throw new RuntimeException(e);
            }
        }

        public static void delete(Connection conn, int id) {
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

        /**
         * Update any fields provided in the map.
         * Keys must match column names exactly.
         */
        public void update(Connection conn, Map<String, Object> fields) {
            if (fields == null || fields.isEmpty()) {
                log.warn("No fields provided to update for Schedule id={}", id);
                return;
            }
            StringBuilder sql = new StringBuilder("UPDATE schedule SET ");
            List<Object> values = new ArrayList<>();

            for (String field : fields.keySet()) {
                sql.append(field).append(" = ?, ");
                values.add(fields.get(field));
            }

            // Remove trailing comma and space
            sql.setLength(sql.length() - 2);
            sql.append(" WHERE id = ?");
            values.add(id);

            try (PreparedStatement p = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < values.size(); i++) {
                    Object val = values.get(i);
                    if (val instanceof String) {
                        p.setString(i + 1, (String) val);
                    } else if (val instanceof Integer) {
                        p.setInt(i + 1, (Integer) val);
                    } else if (val instanceof Double) {
                        p.setDouble(i + 1, (Double) val);
                    } else {
                        p.setObject(i + 1, val);
                    }
                }

                int rowsUpdated = p.executeUpdate();
                if (rowsUpdated == 0) {
                    log.warn("No Schedule record updated for id={}", id);
                } else {
                    log.info("Schedule updated, id={}, fields={}", id, fields.keySet());

                    // Reflect changes in this object's fields
                    for (Map.Entry<String, Object> entry : fields.entrySet()) {
                        String field = entry.getKey();
                        Object value = entry.getValue();
                        switch (field) {
                            case "course_code":
                                this.courseCode = (String) value;
                                break;
                            case "course_name":
                                this.courseName = (String) value;
                                break;
                            case "section":
                                this.section = (String) value;
                                break;
                            case "session":
                                this.session = (String) value;
                                break;
                            case "credits":
                                this.credits = (Double) value;
                                break;
                            case "campus":
                                this.campus = (String) value;
                                break;
                            case "instructor":
                                this.instructor = (String) value;
                                break;
                            case "times":
                                this.times = (String) value;
                                break;
                            case "location":
                                this.location = (String) value;
                                break;
                            case "semester":
                                this.semester = (String) value;
                                break;
                            default:
                                log.warn("Unknown field '{}' in update for Schedule id={}", field, id);
                        }
                    }
                }
            } catch (SQLException e) {
                log.error("Update Schedule failed", e);
                throw new RuntimeException(e);
            }
        }

        public static Schedule selectById(Connection conn, int id) {
            String sql = "SELECT * FROM schedule WHERE id = ?";
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                p.setInt(1, id);
                try (ResultSet rs = p.executeQuery()) {
                    if (!rs.next()) return null;

                    Schedule s = new Schedule(
                            rs.getString("course_code"),
                            rs.getString("course_name"),
                            rs.getString("section"),
                            rs.getString("session"),
                            rs.getDouble("credits"),
                            rs.getString("campus"),
                            rs.getString("instructor"),
                            rs.getString("times"),
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
}