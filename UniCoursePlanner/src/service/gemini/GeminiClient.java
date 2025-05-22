package service.gemini;

import service.Environment;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeminiClient {
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";

    private final String apiKey;
    private final HttpClient httpClient;

    public GeminiClient() {
        this.apiKey = Environment.get("GEMINI_API_KEY");
        this.httpClient = HttpClient.newHttpClient();
    }

    public String sendPrompt(String prompt) {
        String requestBody = String.format("""
        {
          "contents": [
            {
              "parts": [
                { "text": "%s" }
              ]
            }
          ]
        }
        """, prompt.replace("\"", "\\\"")); // escape quotes

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + apiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Extract only the response text from JSON
            return extractTextFromResponse(response.body());

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String extractTextFromResponse(String responseJson) {
        try {
            // Very basic JSON parsing without external libraries
            int textStart = responseJson.indexOf("\"text\":");
            if (textStart == -1) return "No text found in response.";

            int firstQuote = responseJson.indexOf('"', textStart + 7);
            int secondQuote = responseJson.indexOf('"', firstQuote + 1);

            return responseJson.substring(firstQuote + 1, secondQuote)
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"");
        } catch (Exception e) {
            return "Failed to parse response.";
        }
    }
}
