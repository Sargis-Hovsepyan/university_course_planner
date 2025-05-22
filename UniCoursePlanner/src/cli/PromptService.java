package cli;

import java.util.*;

import service.gemini.CoursePlannerAssistant;
import service.gemini.GeminiClient;

public class PromptService{
	private final Scanner scanner = new Scanner(System.in);
	CoursePlannerAssistant assistant = new CoursePlannerAssistant();

	public void collectInitialPreferences(UserPreferences prefs){
		String degree = promptDegree();

		prefs.add("degree_program", degree);
		prefs.add("level", promptYearLevel(degree.toLowerCase()));

		prefs.add("student_name", promptName());
		prefs.add("semester", promptSemester());
		prefs.add("preferred_times", promptPreferredTimes());
		prefs.add("preferred_days", promptPreferredDays());
		prefs.add("completed_courses", promptCompletedCourses());

		String lecturers = promptLecturerPreference();
		if (lecturers != null){
			prefs.add("preferred_instructor", lecturers);
		}

		int[] credits = promptCreditRange();
		prefs.add("min_credits", String.valueOf(credits[0]));
		prefs.add("max_credits", String.valueOf(credits[1]));
	}

	public CommandType promptMainCommand(){
		while(true){
			CommandTranslator.printCommands();
			CommandType command = CommandTranslator.translate(Integer.parseInt(scanner.nextLine().trim()));
			if(command == CommandType.INVALID){
				System.out.println("Invalid command.");
				continue;
			}
			return command;
		}
	}

	public String promptCompletedCourses() {
		System.out.print("Enter completed courses (comma separated, e.g., CS101,MATH101): ");
		return scanner.nextLine().trim();
	}

	public void handleGeneratePlan(UserPreferences prefs){
		while(true){
			System.out.println("===== Generated Plan =====");
			System.out.println(prefs.getPromptText());
			System.out.println("This is the generated plan.");
			GeminiClient client = new GeminiClient();
//			String response = client.sendPrompt(prefs.getPromptText());
			String response = assistant.getCoursePlan(prefs.getPromptMap());

			System.out.println(response);
			System.out.println("==========================");

			System.out.print("Is this plan good? (yes / regenerate / exit): ");
			String answer = scanner.nextLine().trim().toLowerCase();
			if (answer.equals("yes") || answer.equals("exit")) break;
		}
	}

	public String promptPreferredDays() {
		System.out.print("Enter preferred days (comma separated, e.g., MON,WED,FRI): ");
		return scanner.nextLine().trim();
	}

	public void handlePreferences(UserPreferences prefs){
		if(promptYes("Do you have preferred core courses?")){
			System.out.println("Available: CS100, CS306, CS220, CS401");
			System.out.print("Enter selected core courses (comma separated): ");
			String input = scanner.nextLine().trim();
			prefs.add("preferred_courses", input);
		}

		if(promptYes("Do you have preferred general education courses?")){
			System.out.print("Enter general education courses (comma separated): ");
			String input = scanner.nextLine().trim();
			prefs.add("preferred_gen_ed_area", input);
		}
	}

	public String promptPreferredTimes() {
		System.out.print("Please enter your preferred times (e.g., 9:00AM-1:00PM): ");
		return scanner.nextLine().trim();
	}

	public void handleFreePrompt(UserPreferences prefs){
		if(promptYes("Do we consider previous prompts?")){
			System.out.println("=== Using previous prompts ===");
			System.out.println(prefs.getPromptText());
		} else{
			prefs.clearPrompt();
			System.out.println("Starting new free prompt...");
		}

		System.out.print("Enter your free-form request: ");
		String freeText = scanner.nextLine().trim();

		prefs.add("free_description", freeText);

		System.out.println("===== Final Free Prompt =====");
		System.out.print(prefs.getPromptText());
		System.out.println("User free request: " + freeText);
	}

	private String promptDegree() {
		while (true) {
			System.out.print("Enter your degree program (e.g., CS, DS, MSCIS): ");
			String input = scanner.nextLine().trim().toUpperCase();

			if (!input.isEmpty()) {
				return input;
			}

			System.out.println("Invalid input. Please enter a non-empty degree program.");
		}
	}

	private String promptYearLevel(String degree){
		List<String> options = List.of("Freshman", "Sophomore", "Junior", "Senior");

		while(true){
			System.out.print("What year are you in? " + options + ": ");
			String input = scanner.nextLine().trim();
			if (options.contains(input)) return input;
			System.out.println("Invalid input.");
		}
	}
	
	public String promptName(){
		System.out.print("Please enter your name: ");
		return scanner.nextLine().trim();
	}

	public String promptSemester(){
		System.out.print("Please enter semester (e.g., 202425/fall): ");
		return scanner.nextLine().trim();
	}

	private String promptLecturerPreference(){
		if(promptYes("Do you have a preferred instructor?")){
			System.out.print("Enter name(s), comma-separated: ");
			return scanner.nextLine().trim();
		}
		return null;
	}

	private int[] promptCreditRange(){
		int min = -1, max = -1;
		while(true){
			try{
				System.out.print("Enter minimum credits: ");
				min = Integer.parseInt(scanner.nextLine());
				System.out.print("Enter maximum credits: ");
				max = Integer.parseInt(scanner.nextLine());
				if (max >= min) break;
				else System.out.println("Max must be ≥ Min.");
			} catch(NumberFormatException e){
				System.out.println("Invalid number.");
			}
		}
		return new int[]{min, max};
	}

	private boolean promptYes(String question){
		System.out.print(question + " (yes / no): ");
		return scanner.nextLine().trim().equalsIgnoreCase("yes");
	}

	private String capitalize(String word){
		return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
	}
}
