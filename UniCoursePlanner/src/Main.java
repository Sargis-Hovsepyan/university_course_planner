package service;

import service.gemini.GeminiClient;

// THIS IS NOT FINAL MAIN. JUST A TEST

public class Main {
    public static void main(String[] args) {
        GeminiClient geminiClient = new GeminiClient();
        String prompt = "Tell me the capital of France";
        String response = geminiClient.sendPrompt(prompt);
        System.out.println("Gemini Response:\n" + response);
    }
}
