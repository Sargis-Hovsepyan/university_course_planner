package cli;

import java.util.*;

public class PromptService{
	private final Scanner scanner = new Scanner(System.in);

	public void collectInitialPreferences(UserPreferences prefs){
		DegreeType degree = promptDegree();
		prefs.add("Degree", capitalize(degree.name()));
		prefs.add("Degree course", promptYearLevel(degree));

		String lecturers = promptLecturerPreference();
		if (lecturers != null){
			prefs.add("Preferred lecturer", lecturers);
		}

		int[] credits = promptCreditRange();
		prefs.add("Preferred minimum number of credits", String.valueOf(credits[0]));
		prefs.add("Preferred maximum number of credits", String.valueOf(credits[1]));
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

	public void handleGeneratePlan(UserPreferences prefs){
		while(true){
			System.out.println("===== Generated Plan =====");
			System.out.println(prefs.getPromptText());
			System.out.println("This is the generated plan.");
			System.out.println("==========================");

			System.out.print("Is this plan good? (yes / regenerate / exit): ");
			String answer = scanner.nextLine().trim().toLowerCase();
			if (answer.equals("yes") || answer.equals("exit")) break;
		}
	}

	public void handlePreferences(UserPreferences prefs){
		if(promptYes("Do you have preferred core courses?")){
			System.out.println("Available: CS100, CS306, CS220, CS401");
			System.out.print("Enter selected core courses (comma separated): ");
			String input = scanner.nextLine().trim();
			prefs.add("Preferred core courses", input);
		}

		if(promptYes("Do you have preferred general education courses?")){
			System.out.print("Enter general education courses (comma separated): ");
			String input = scanner.nextLine().trim();
			prefs.add("Preferred courses for general education", input);
		}
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

		System.out.println("===== Final Free Prompt =====");
		System.out.print(prefs.getPromptText());
		System.out.println("User free request: " + freeText);
	}

	private DegreeType promptDegree(){
		while(true){
			System.out.print("Enter your degree (Bachelor / Master): ");
			String input = scanner.nextLine().trim().toLowerCase();
			return switch(input){
				case "bachelor" -> DegreeType.BACHELOR;
				case "master" -> DegreeType.MASTER;
					default -> {
						System.out.println("Invalid input.");
						yield promptDegree();
					}
			};
		}
	}

	private String promptYearLevel(DegreeType degree){
		List<String> options = (degree == DegreeType.BACHELOR) ?
			List.of("Freshman", "Sophomore", "Junior", "Senior") :
			List.of("Junior", "Senior");

		while(true){
			System.out.print("What year are you in? " + options + ": ");
			String input = scanner.nextLine().trim();
			if (options.contains(input)) return input;
			System.out.println("Invalid input.");
		}
	}

	private String promptLecturerPreference(){
		if(promptYes("Do you have a preferred lecturer?")){
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
