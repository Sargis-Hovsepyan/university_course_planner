package service;

import service.gemini.CoursePlannerAssistant;

import java.util.*;

public class Test {
    public static void main(String[] args) {
        CoursePlannerAssistant assistant = new CoursePlannerAssistant();

        // Sample input data
        Map<String, String> inputParams = new HashMap<>();
        inputParams.put("student_name", "Alice");
        inputParams.put("degree_program", "CS"); // Matches program.code
        inputParams.put("level", "Sophomore");
        inputParams.put("completed_courses", "CS101,MATH101");
        inputParams.put("semester", "202425/fall");
        inputParams.put("preferred_times", "morning");
        inputParams.put("preferred_instructor", "Dr. Smith,Dr. Lee");
        inputParams.put("preferred_days", "MON,WED,FRI");
        inputParams.put("free_description", "AI");
        inputParams.put("min_credits", "15");
        inputParams.put("max_credits", "15");
        inputParams.put("preferred_gen_ed_area", "Literature");

        // Run the assistant
        String coursePlan = assistant.getCoursePlan(inputParams);

        // Print result
        System.out.println("🔍 Gemini-Generated Course Plan:\n");
        System.out.println(coursePlan);
    }
}