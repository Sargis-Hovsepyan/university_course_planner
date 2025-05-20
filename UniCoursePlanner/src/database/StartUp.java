package database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import database.Tables.Instructor;
import database.Tables.Course;
import database.Tables.DegreeProgram;
import database.Tables.Schedule;


public class StartUp {

    private static StartUp instance;

    private StartUp() {
    }

    public static StartUp getInstance() {
        if (instance == null) {
            synchronized (StartUp.class) {
                if (instance == null) {
                    instance = new StartUp();
                }
            }
        }
        return instance;
    }

    public void readInstructors(String csvFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String headerLine = br.readLine(); // skip header

            if (headerLine == null) {
                System.err.println("CSV is empty");
                return;
            }

            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (fields.length < 2) {
                    System.err.println("Invalid CSV row, skipping: " + line);
                    continue;
                }
                int id = Integer.parseInt(fields[0].trim());
                String name = fields[1].trim();

                Instructor ins = new Instructor(id, name);
                ins.insert();
            }
            System.out.println("Finished inserting instructors");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readDegreePrograms(String csvPath) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String header = br.readLine(); // skip header
            if (header == null) {
                System.out.println("Degree programs CSV file is empty");
                return;
            }

            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);// assuming tab-separated
                if (fields.length < 3) {
                    System.out.println("Skipping malformed line: " + line);
                    continue;
                }

                String code = fields[0].trim();
                String name = fields[1].trim();
                String type = fields[2].trim();

                DegreeProgram program = new DegreeProgram(code, name, type);
                program.insert();
            }

            System.out.println("Finished loading degree programs from CSV.");
        } catch (IOException e) {
            System.err.println("Error reading degree programs CSV file: " + e.getMessage());
        }
    }

    public void readCourses(String csvPath) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            // Read header line and ignore
            String header = br.readLine();
            if (header == null) {
                System.out.println("CSV file is empty");
                return;
            }

            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                if (fields.length < 6) {
                    System.out.println("Skipping malformed line: " + line);
                    continue;
                }

                String program = fields[0].trim();
                String code = fields[1].trim();
                String title = fields[2].trim();
                String description = fields[3].trim();
                String creditsStr = fields[4].trim();
                String prerequisite = fields[5].trim();

                Double credits = null;
                if (!creditsStr.isEmpty()) {
                    try {
                        credits = Double.parseDouble(creditsStr);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid credits value for line: " + line);
                        // leave credits as null
                    }
                }

                Course ins = new Course(code, program, title, description, credits, prerequisite);
                ins.insert();
            }
            System.out.println("Finished loading courses from CSV.");
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
    }

    public void readSchedules(String csvPath) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String header = br.readLine(); // skip header
            if (header == null) {
                System.out.println("Schedule CSV file is empty");
                return;
            }

            String line;
            while ((line = br.readLine()) != null) {
                // Assuming tab-separated values
                String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (fields.length < 10) {
                    System.out.println("Skipping malformed line: " + line);
                    continue;
                }

                String courseCode = fields[0].trim();   // program/course code
                String courseName = fields[1].trim();   // course name
                String section = fields[2].trim();      // section as String

                double credits = 0.0;
                try {
                    credits = Double.parseDouble(fields[4].trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid credits in line: " + line);
                }

                String session = fields[3].trim();
                String campus = fields[5].trim();
                String instructor = fields[6].trim();
                String times = fields[7].trim();
                String location = fields[8].trim();
                String semester = fields[9].trim();

                Schedule schedule = new Schedule(courseCode, courseName, section, session, credits,
                        campus, instructor, times, location, semester);
                schedule.insert();
            }

            System.out.println("Finished loading schedules from CSV.");
        } catch (IOException e) {
            System.err.println("Error reading schedule CSV file: " + e.getMessage());
        }
    }
}
