package service.gemini;

import database.DatabaseManager;
import database.Tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoursePlannerAssistant {
    private static final Logger log = LoggerFactory.getLogger(CoursePlannerAssistant.class);
    private final GeminiClient geminiClient;
    private final DatabaseManager dbManager;

    public CoursePlannerAssistant() {
        this.geminiClient = new GeminiClient();
        this.dbManager = new DatabaseManager();
        Tables.setConnection(dbManager.getConnection());
    }

    public String getCoursePlan(Map<String, String> inputParams) {
        // Extract parameters with defaults
        String semester = inputParams.getOrDefault("semester", "202425/fall");
        String yearLevel = inputParams.getOrDefault("level", "Sophomore");
        String degreeProgram = inputParams.getOrDefault("degree_program", "CS");
        String studentName = inputParams.getOrDefault("student_name", "Student");
        String preferredTimes = inputParams.getOrDefault("preferred_times", "");
        String preferredInstructor = inputParams.getOrDefault("preferred_instructor", "");
        String minCredits = inputParams.getOrDefault("min_credits", "9");
        String maxCredits = inputParams.getOrDefault("max_credits", "15");
        String freeDescription = inputParams.getOrDefault("free_description", "");
        String preferredCourses = inputParams.getOrDefault("preferred_courses", "");
        String preferredGenEdArea = inputParams.getOrDefault("preferred_gen_ed_area", "");

        // Parse comma-separated lists
        List<String> completedCourses = new ArrayList<>();
        if (inputParams.containsKey("completed_courses") && !inputParams.get("completed_courses").isEmpty()) {
            completedCourses = List.of(inputParams.get("completed_courses").split("\\s*,\\s*"));
        }

        Set<String> preferredInstructors = new HashSet<>();
        if (!preferredInstructor.isEmpty()) {
            preferredInstructors = Set.of(preferredInstructor.split("\\s*,\\s*"));
        }

        Set<String> preferredDays = new HashSet<>();
        if (inputParams.containsKey("preferred_days") && !inputParams.get("preferred_days").isEmpty()) {
            preferredDays = Set.of(inputParams.get("preferred_days").split("\\s*,\\s*"));
        }

        // Build preferences map
        Map<String, String> preferences = new HashMap<>();
        if (!freeDescription.isEmpty()) {
            preferences.put("Interest", freeDescription);
        }
        if (!preferredCourses.isEmpty()) {
            preferences.put("Preferred Courses", preferredCourses);
        }
        if (!preferredGenEdArea.isEmpty()) {
            preferences.put("Preferred GenEd Area", preferredGenEdArea);
        }
        if (!minCredits.equals(maxCredits)) {
            preferences.put("Credit Range", minCredits + "-" + maxCredits + " credits");
        } else {
            preferences.put("Credit Load", maxCredits + " credits");
        }

        // Fetch available course schedules for the specified semester
        Map<String, String> courseScheduleDB = fetchCourseSchedules(semester);

        // Build prompt
        StringBuilder prompt = buildPrompt(
                studentName, degreeProgram, yearLevel, completedCourses,
                preferences, preferredInstructors, preferredDays, preferredTimes,
                courseScheduleDB
        );

        // Send prompt and get response
        String response = geminiClient.sendPrompt(prompt.toString());

        // Close database connection
        dbManager.connClose();

        // Return the plain text response from Gemini
        return response;
    }

    private String formatResponseAsTable(String response) {
        StringBuilder table = new StringBuilder();
        table.append("<table border='1'>\n");
        table.append("<tr><th>Course Code</th><th>Course Name</th><th>Credits</th><th>Instructor</th><th>Times</th></tr>\n");

        String justification = "No justification provided.";
        String totalCredits = "0";

        // Parse the response (assuming it follows the expected format)
        String[] lines = response.split("\n");
        boolean inScheduleSection = false;
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("Recommended Semester Plan:")) {
                inScheduleSection = true;
                continue;
            }
            if (line.startsWith("Total Credits:")) {
                totalCredits = line.replace("Total Credits:", "").trim();
                continue;
            }
            if (line.startsWith("Justification:")) {
                justification = line.replace("Justification:", "").trim();
                continue;
            }
            if (inScheduleSection && line.startsWith("-") && !line.equals("- None")) {
                // Parse course line, e.g., "- CS201: Data Structures (3.0 credits, Dr. Smith, MON WED FRI 09:00AM-10:00AM)"
                try {
                    String courseInfo = line.substring(2).trim(); // Remove "- "
                    String[] parts = courseInfo.split(":\\s+|\\s*\\(");
                    String courseCode = parts[0].trim();
                    String courseName = parts[1].split("\\s*\\(")[0].trim();
                    String details = parts[1].substring(parts[1].indexOf("(") + 1, parts[1].length() - 1);
                    String[] detailParts = details.split(",\\s*");
                    String credits = detailParts[0].replace("credits", "").trim();
                    String instructor = detailParts[1].trim();
                    String times = detailParts[2].trim();

                    table.append("<tr>");
                    table.append("<td>").append(courseCode).append("</td>");
                    table.append("<td>").append(courseName).append("</td>");
                    table.append("<td>").append(credits).append("</td>");
                    table.append("<td>").append(instructor).append("</td>");
                    table.append("<td>").append(times).append("</td>");
                    table.append("</tr>\n");
                } catch (Exception e) {
                    log.error("Failed to parse course line: {}", line, e);
                }
            }
        }
        table.append("</table>\n");
        table.append("<p><strong>Total Credits:</strong> ").append(totalCredits).append("</p>\n");
        table.append("<p><strong>Justification:</strong> ").append(justification).append("</p>\n");

        return table.toString();
    }

    private Map<String, String> fetchCourseSchedules(String semester) {
        Map<String, String> courseScheduleDB = new HashMap<>();
        // Fetch all schedules for the semester
        List<Tables.Schedule> schedules = Tables.Schedule.selectBySemester(semester);
        try {
            // Format all schedules
            for (Tables.Schedule s : schedules) {
                String times = s.times.replaceAll("(\\d):(\\d{2})", "0$1:$2");
                String scheduleInfo = String.format("%s: %s - %s %s by %s (%.1f credits)",
                        s.courseCode, s.courseName, times, s.semester, s.instructor, s.credits);
                courseScheduleDB.put(s.courseCode, scheduleInfo);
            }
        } catch (Exception e) {
            log.error("Failed to format course schedules for semester {}", semester, e);
            throw new DatabaseManager.DatabaseException("Failed to format course schedules", e);
        }
        return courseScheduleDB;
    }

    private StringBuilder buildPrompt(
            String studentName,
            String degreeProgram,
            String yearLevel,
            List<String> completedCourses,
            Map<String, String> preferences,
            Set<String> preferredInstructors,
            Set<String> preferredDays,
            String preferredTimeSlot,
            Map<String, String> courseScheduleDB
    ) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert university course planner tasked with creating an optimized, conflict-free semester schedule for a student. Your goal is to select courses that align with the student's degree program, infer core and General Education courses, match the requested credit load, and respect preferences. Follow these instructions precisely:\n\n");

        prompt.append("### Student Profile\n");
        prompt.append("- **Name**: ").append(studentName).append("\n");
        prompt.append("- **Degree Program**: ").append(degreeProgram).append(" (e.g., Computer Science, code: CS)\n");
        prompt.append("- **Year Level**: ").append(yearLevel).append(" (e.g., Sophomore)\n");

        prompt.append("\n### Completed Courses\n");
        prompt.append("These courses may influence eligibility for advanced courses:\n");
        if (completedCourses.isEmpty()) {
            prompt.append("- None\n");
        } else {
            for (String course : completedCourses) {
                prompt.append("- ").append(course).append("\n");
            }
        }

        prompt.append("\n### Student Preferences\n");
        prompt.append("Prioritize courses that match these preferences:\n");
        if (preferences.isEmpty()) {
            prompt.append("- None\n");
        } else {
            for (Map.Entry<String, String> entry : preferences.entrySet()) {
                prompt.append("- **").append(entry.getKey()).append("**: ").append(entry.getValue()).append("\n");
            }
        }
        if (!preferredInstructors.isEmpty()) {
            prompt.append("- **Preferred Instructors**: ").append(String.join(", ", preferredInstructors)).append("\n");
        }
        if (!preferredDays.isEmpty()) {
            prompt.append("- **Preferred Days**: ").append(String.join(", ", preferredDays)).append(" (e.g., MON, WED, FRI)\n");
        }
        if (!preferredTimeSlot.isEmpty()) {
            prompt.append("- **Preferred Time Slot**: ").append(preferredTimeSlot).append(" (morning: before 12 PM, afternoon: 12 PM-5 PM, evening: after 5 PM)\n");
        }

        prompt.append("\n### Available Course Schedules\n");
        prompt.append("These are all available courses for the semester. Format is 'CourseCode: CourseName - Times Semester by Instructor (Credits)':\n");
        if (courseScheduleDB.isEmpty()) {
            prompt.append("- None\n");
        } else {
            for (Map.Entry<String, String> entry : courseScheduleDB.entrySet()) {
                prompt.append("- ").append(entry.getValue()).append("\n");
            }
        }

        prompt.append("\n### Instructions\n");
        prompt.append("1. **Course Selection**:\n");
        prompt.append("   - Select courses from the available schedules that are appropriate for the student's degree program (code: ").append(degreeProgram).append(") and year level.\n");
        prompt.append("   - Infer core courses for the degree program based on course codes (e.g., CS201 for CS) or names (e.g., 'Data Structures' for CS).\n");
        prompt.append("   - Infer General Education (GenEd) courses based on codes (e.g., GEN101) or names (e.g., 'General Education Literature') that are broadly applicable across programs.\n");
        prompt.append("   - Prioritize core courses for the degree program, but include GenEd courses to meet exact credit requirements, align with the student's interest, or diversify the schedule.\n");
        prompt.append("   - If an interest is specified (e.g., AI), prioritize courses whose names suggest relevance (e.g., 'Intro to Artificial Intelligence').\n");
        prompt.append("   - Favor courses with preferred instructors, days, and time slots, but include others if necessary to meet credit and scheduling requirements.\n");
        prompt.append("2. **Exact Credit Requirements**:\n");
        prompt.append("   - Select courses that match the requested credit load based on the workload preference:\n");
        prompt.append("     - light: Exactly 9 credits\n");
        prompt.append("     - medium: Exactly 12 credits\n");
        prompt.append("     - full: Exactly 15 credits\n");
        prompt.append("   - If the workload preference is missing or invalid, return an error in the justification.\n");
        prompt.append("3. **Conflict Avoidance**:\n");
        prompt.append("   - Ensure no time conflicts: courses on the same day must have non-overlapping times (e.g., 'MON 09:00AM-10:00AM' and 'MON 10:00AM-11:00AM' are fine).\n");
        prompt.append("4. **Output Format**:\n");
        prompt.append("   - Return the schedule in this exact format:\n");
        prompt.append("     ```\n");
        prompt.append("     Recommended Semester Plan:\n");
        prompt.append("     - Course Code: Course Name (Credits, Instructor, Times)\n");
        prompt.append("     Total Credits: X\n");
        prompt.append("     Justification: [Explain why these courses were chosen, including inferred core/GenEd status, credit matching, and preference alignment]\n");
        prompt.append("     ```\n");
        prompt.append("5. **Edge Cases**:\n");
        prompt.append("   - If no valid schedule is possible (e.g., no available courses, unable to match exact credits, or all have conflicts), return:\n");
        prompt.append("     ```\n");
        prompt.append("     Recommended Semester Plan:\n");
        prompt.append("     - None\n");
        prompt.append("     Total Credits: 0\n");
        prompt.append("     Justification: [Explain why no schedule could be created, e.g., invalid workload, no eligible courses, or conflicts]\n");
        prompt.append("     ```\n");

        prompt.append("\n### Example Output\n");
        prompt.append("For a student in CS, Sophomore, with workload=light, interest=AI, preferences for morning classes, and available courses:\n");
        prompt.append("- CS201: Data Structures - MON WED FRI 09:00AM-10:00AM 202425/fall by Dr. Smith (3.0 credits)\n");
        prompt.append("- MATH201: Calculus II - TUE THU 11:00AM-12:30PM 202425/fall by Dr. Adams (3.0 credits)\n");
        prompt.append("- AI101: Intro to Artificial Intelligence - MON WED FRI 11:00AM-12:00PM 202425/fall by Dr. Lee (3.0 credits)\n");
        prompt.append("- GEN101: General Education Literature - TUE THU 10:00AM-11:30AM 202425/fall by Dr. Brown (3.0 credits)\n");
        prompt.append("Return:\n");
        prompt.append("```\n");
        prompt.append("Recommended Semester Plan:\n");
        prompt.append("- CS201: Data Structures (3.0 credits, Dr. Smith, MON WED FRI 09:00AM-10:00AM)\n");
        prompt.append("- MATH201: Calculus II (3.0 credits, Dr. Adams, TUE THU 11:00AM-12:30PM)\n");
        prompt.append("- GEN101: General Education Literature (3.0 credits, Dr. Brown, TUE THU 10:00AM-11:30AM)\n");
        prompt.append("Total Credits: 9.0\n");
        prompt.append("Justification: CS201 is inferred as a core course for Computer Science based on its code and name, fitting the morning preference and preferred instructor (Dr. Smith). MATH201 is a core course relevant to CS, aligning with morning times. GEN101 is inferred as a General Education course, included to meet the exact 9-credit requirement for a light workload and diversify the schedule. All courses are conflict-free and match preferred days (MON, WED, FRI, TUE, THU).\n");
        prompt.append("```\n");

        prompt.append("\nNow, generate the semester plan for the student based on the provided data.");
        return prompt;
    }

    public String generateOptimizedPlan(
            String studentName,
            String degreeProgram,
            String yearLevel,
            List<String> completedCourses,
            Map<String, String> preferences,
            Set<String> preferredInstructors,
            Set<String> preferredDays,
            String preferredTimeSlot
    ) {
        // Initialize inputs with defaults
        if (studentName == null || studentName.isEmpty()) studentName = "Student";
        if (degreeProgram == null || degreeProgram.isEmpty()) degreeProgram = "CS";
        if (yearLevel == null || yearLevel.isEmpty()) yearLevel = "Sophomore";
        if (completedCourses == null) completedCourses = new ArrayList<>();
        if (preferences == null) preferences = new HashMap<>();
        if (preferredInstructors == null) preferredInstructors = Set.of();
        if (preferredDays == null) preferredDays = Set.of();
        if (preferredTimeSlot == null) preferredTimeSlot = "";

        // Fetch available course schedules from database
        Map<String, String> courseScheduleDB = fetchCourseSchedules("202425/fall");

        // Build prompt
        StringBuilder prompt = buildPrompt(
                studentName, degreeProgram, yearLevel, completedCourses,
                preferences, preferredInstructors, preferredDays, preferredTimeSlot,
                courseScheduleDB
        );

        // Send prompt and get response
        String response = geminiClient.sendPrompt(prompt.toString());

        // Close database connection
        dbManager.connClose();
        return response;
    }
}