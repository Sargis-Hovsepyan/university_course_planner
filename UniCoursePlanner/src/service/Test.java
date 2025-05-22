package service;

import service.gemini.CoursePlannerAssistant;

import java.util.*;

public class Test {
    public static void main(String[] args) {
        CoursePlannerAssistant assistant = new CoursePlannerAssistant();

        // Sample input data
        String studentName = "Alice";
        String degreeProgram = "CS"; // Matches program.code
        String yearLevel = "Sophomore";
        List<String> completedCourses = List.of("CS101", "MATH101");

        Map<String, String> preferences = new HashMap<>();
        preferences.put("interest", "AI");
        preferences.put("workload", "full");
        preferences.put("semester", "Fall 2025");

        Set<String> preferredInstructors = new HashSet<>(Set.of("Dr. Smith", "Dr. Lee"));
        Set<String> preferredDays = new HashSet<>(Set.of("MON", "WED", "FRI"));
        String preferredTimeSlot = "morning";

        // Run the assistant
        String optimizedPlan = assistant.generateOptimizedPlan(
                studentName,
                degreeProgram,
                yearLevel,
                completedCourses,
                preferences,
                preferredInstructors,
                preferredDays,
                preferredTimeSlot
        );

        // Print result
        System.out.println("🔍 Gemini-Generated Course Plan:\n");
        System.out.println(optimizedPlan);
    }
}