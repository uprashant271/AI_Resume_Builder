package com.resume.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Service
public class ResumeServiceImpl implements ResumeService {

    @Value("${openrouter.api.key}")
    private String openRouterApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> generateResumeResponse(String userResumeDescription) throws IOException, InterruptedException {
        String promptText = """
            Generate a professional IT job resume in JSON format based on the following description. Ensure the JSON is consistent, well-structured, and contains all specified keys, even if some values are empty or null. Use the exact keys provided below and maintain their hierarchy.

            Input Description:
            "{{userDescription}}"

            JSON Structure Requirements:
            personalInformation: Include the following keys:
            fullName (string)
            email (string)
            phoneNumber (string)
            location (string)
            linkedIn (string or null if not provided)
            gitHub (string or null if not provided)
            portfolio (string or null if not provided)
            summary: A brief overview of skills, experience, and career goals (string).
            skills: List of object that contain two keys 'title' and 'level'

            experience: A list of job roles. Each job role should include:
            jobTitle (string)
            company (string)
            location (string)
            duration (string, e.g., "Jan 2020 - Present")
            responsibility(string)

            education: A list of degrees. Each degree should include:
            degree (string)
            university (string)
            location (string)
            graduationYear (string)

            certifications: A list of certifications. Each certification should include:
            title (string)
            issuingOrganization (string)
            year (string)

            projects: A list of key projects. Each project should include:
            title (string)
            description (string)
            technologiesUsed (array of strings)
            githubLink (string or null if not provided)

            achievements: A list achievements that contains objects of keys
            title (string)
            year(string)
            extraInformation(string)

            languages: A list of spoken languages objects contain keys
            id(number)
            name(string)

            interests: A list of additional interests or hobbies related to technology or professional development  [list of objects having keys].
            id(number)
            name(string)
            """;

        String prompt = putValuesToTemplate(promptText, Map.of("userDescription", userResumeDescription));

        // Prepare JSON payload for OpenRouter
        Map<String, Object> message = Map.of("role", "user", "content", prompt);
        Map<String, Object> body = Map.of(
                "model", "deepseek/deepseek-r1-zero:free",
                "messages", new Object[]{message}
        );

        String requestBody = objectMapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://openrouter.ai/api/v1/chat/completions"))
                .header("Authorization", "Bearer " + openRouterApiKey)
                .header("Content-Type", "application/json")
                .header("X-Title", "ResumeAI")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Extract and parse the response
        String jsonResponse = extractResponseContent(response.body());
        System.out.println("Raw AI Response:\n" + jsonResponse);

        Map<String, Object> mapResponse = parseJson(jsonResponse);
        return mapResponse;
    }

    private String putValuesToTemplate(String template, Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            template = template.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return template;
    }

    private String extractResponseContent(String response) throws IOException {
        Map<String, Object> fullResponse = objectMapper.readValue(response, new TypeReference<>() {});
        var choices = (java.util.List<Map<String, Object>>) fullResponse.get("choices");
        if (choices != null && !choices.isEmpty()) {
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return message != null ? (String) message.get("content") : "";
        }
        return "";
    }

    private Map<String, Object> parseJson(String content) {
        Map<String, Object> result = new HashMap<>();
        try {
            // Try extracting content from code block if present
            int start = content.indexOf("```json");
            int end = content.lastIndexOf("```");
            if (start != -1 && end != -1 && end > start) {
                content = content.substring(start + 7, end).trim();
            }
            result = objectMapper.readValue(content, new TypeReference<>() {});
        } catch (Exception e) {
            System.err.println("Failed to parse JSON: " + e.getMessage());
        }
        return result;
    }
}
