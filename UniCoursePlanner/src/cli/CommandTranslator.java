package cli;

import java.util.LinkedHashMap;
import java.util.Map;

public class CommandTranslator{

    	private static final Map<Integer, CommandType> commandMap = new LinkedHashMap<>();
	
	static{
		commandMap.put(1, CommandType.GENERATE_PLAN);
		commandMap.put(2, CommandType.PREFERENCES);
		commandMap.put(3, CommandType.FREE_PROMPT);
		commandMap.put(4, CommandType.EXIT);
		commandMap.put(5, CommandType.INVALID);
}

	public static void printCommands(){
		int size = commandMap.size();
		int index = 0;
            	System.out.println("Choose one: ");
		for (Map.Entry<Integer, CommandType> entry : commandMap.entrySet()) {
			if (index == size - 1) break;
			System.out.print("[" + entry.getKey() + "]" + " -> " + entry.getValue().name() + " / ");
			index++;
		}
	}

	public static CommandType translate(Integer cmdNumber){
		switch (cmdNumber){
			case 1: return CommandType.GENERATE_PLAN;
			case 2: return CommandType.PREFERENCES;
			case 3: return CommandType.FREE_PROMPT;
			case 4: return CommandType.EXIT;
			default: return CommandType.INVALID;
		}
	}

}
