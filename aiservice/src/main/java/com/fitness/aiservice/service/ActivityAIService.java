package com.fitness.aiservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {

    private final GeminiService geminiService;

    public Recommendation getActivityRecommendation(Activity activity) {
        String prompt = createPromptForActivity(activity);
        String aiResponse = geminiService.getRecommendation(prompt);
        log.info("Generated Prompt: {}", aiResponse);
        return proccessAIResponse(activity, aiResponse);
    }

    private Recommendation proccessAIResponse(Activity activity, String aiResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(aiResponse);

            JsonNode textNode = rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");

            String jsonContent = textNode.asText()
                    .replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            log.info("Extracted JSON Content: {}", jsonContent);

            JsonNode analysisJson = mapper.readTree(jsonContent);
            JsonNode analysisNode = analysisJson.path("analytics");
            StringBuilder fullAnalysis = new StringBuilder();
            addAnalysisSection(fullAnalysis, analysisNode, "overall", "Overall");
            addAnalysisSection(fullAnalysis, analysisNode, "pace", "Pace");
            addAnalysisSection(fullAnalysis, analysisNode, "heartRate", "Heart Rate");
            addAnalysisSection(fullAnalysis, analysisNode, "coloriesBurned", "Balories");
            List<String> improvements = extractImprovement(analysisJson.path("improvements"));
            List<String> suggestions = extractSuggestion(analysisJson.path("suggestions"));
            List<String> safety = extractSafetyGuideline(analysisJson.path("safety"));

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .type(activity.getActivityType().toString())
                    .recommendationText(fullAnalysis.toString().trim())
                    .improvments(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            // log.error("Failed to process AI response for user {}: {}",
            // activity.getUserId(), e.getMessage(), e);
            e.printStackTrace();
            return createDefaultRecommendation(activity);
        }
    }

    private Recommendation createDefaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .type(activity.getActivityType().toString())
                .recommendationText("Unable to generate detailed analysis")
                .improvments(Collections.singletonList("Continue with your current routien"))
                .suggestions(Collections.singletonList("Consider consulting a fitness cosultant"))
                .safety(Arrays.asList(
                        "Always warm up before execise",
                        "stay hydrated",
                        "Listen to ypur body"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<String> extractSafetyGuideline(JsonNode safetyGuidelineNode) {
        List<String> safetyGuidelines = new ArrayList<>();
        if (safetyGuidelineNode.isArray()) {
            safetyGuidelineNode.forEach(item -> safetyGuidelines.add(item.asText()));
        }
        return safetyGuidelines.isEmpty() ? Collections.singletonList("Follow gender safetyGuidelines")
                : safetyGuidelines;
    }

    private List<String> extractSuggestion(JsonNode suggestionNode) {
        List<String> suggestions = new ArrayList<>();
        if (suggestionNode.isArray()) {
            suggestionNode.forEach(suggestion -> {
                String workout = suggestion.path("workout").asText();
                String description = suggestion.path("description").asText();
                suggestions.add(String.format("%s: %s", workout, description));
            });

        }
        return suggestions.isEmpty() ? Collections.singletonList("No specific suggestions provided") : suggestions;
    }

    private List<String> extractImprovement(JsonNode improvmentsNode) {
        List<String> improvments = new ArrayList<>();
        if (improvmentsNode.isArray()) {
            improvmentsNode.forEach(improvment -> {
                String area = improvment.path("area").asText();
                String recommendation = improvment.path("recommendation").asText();
                improvments.add(String.format("%s: %s", area, recommendation));
            });
        }
        return improvments.isEmpty() ? Collections.singletonList("No specific improvement provided") : improvments;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        if (!analysisNode.path(key).isMissingNode()) {
            fullAnalysis.append(prefix).append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
    }

    private String createPromptForActivity(Activity activity) {
        return String.format("""
                        Analyze the following activity details and provide a comprehensive recommendation:
                {
                    "analytics": {
                        "overall": "Uverall analysis here",
                        "pace": "Pace analysis here",
                        "heartRate": "Heart rate analysis here",
                        "coloriesBurned": "Calories analysis here",
                    },
                    "improvements":[
                        {
                          "area":"Aera name",
                          "recommendation":"Detailed Recommendation"
                        }
                    ],
                    "suggestedWorkouts":[
                        {
                          "working":"Workout name",
                          "description":"Detailes workout description",
                        }
                    ],
                    "safety":[
                          "Safety point 1",
                          "Safety point 2"
                    ]
                }
                Analyze this activity:
                Activity Type: %s
                Duration: %d minutes
                colorie Burned: %d
                Additional Matrics: %s
                provide detailed analysis focusing on performance, improvements, next workouts, and safety guidelines.
                Ensure the response follows the EXACT JSON format shown above.
                    """,
                activity.getActivityType().name(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalData());
    }

}
