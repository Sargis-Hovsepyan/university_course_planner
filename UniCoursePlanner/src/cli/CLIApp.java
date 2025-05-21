package cli;

public class CLIApp{
	private final PromptService promptService = new PromptService();
	private final UserPreferences userPreferences = new UserPreferences();

	public void run(){
		promptService.collectInitialPreferences(userPreferences);

		while(true){
			CommandType command = promptService.promptMainCommand();

			switch(command){
				case GENERATE_PLAN -> promptService.handleGeneratePlan(userPreferences);
				case PREFERENCES -> promptService.handlePreferences(userPreferences);
				case FREE_PROMPT -> promptService.handleFreePrompt(userPreferences);
				case EXIT -> {
					System.out.println("Exiting. Goodbye!");
					return;
				}
			}
		}
	}
}

