package cli;

import java.util.*;

public class UserPreferences{
	private StringBuilder fullPrompt = new StringBuilder();
	private final Map<String, String> data = new LinkedHashMap<>();

	public void add(String key, String value){
		data.put(key, value);
		fullPrompt.append(key).append(": ").append(value).append("\n");
	}

	public String getPromptText(){
		return fullPrompt.toString();
	}

	public void clearPrompt(){
		fullPrompt = new StringBuilder();
		data.clear();
	}

	public boolean hasPreferences(){
		return !data.isEmpty();
	}
}
