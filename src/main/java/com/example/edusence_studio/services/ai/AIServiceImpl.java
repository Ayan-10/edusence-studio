package com.example.edusence_studio.services.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {

    @Value("${ai.gemini.api-key:}")
    private String geminiApiKey;

    @Value("${ai.gemini.api-url:https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent}")
    private String geminiApiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Map<String, String> LANGUAGE_NAMES = Map.ofEntries(
            Map.entry("EN", "English"),
            Map.entry("HI", "Hindi"),
            Map.entry("BN", "Bengali"),
            Map.entry("TA", "Tamil"),
            Map.entry("TE", "Telugu"),
            Map.entry("MR", "Marathi"),
            Map.entry("GU", "Gujarati"),
            Map.entry("KN", "Kannada"),
            Map.entry("ML", "Malayalam"),
            Map.entry("OR", "Odia"),
            Map.entry("PA", "Punjabi"),
            Map.entry("AS", "Assamese"),
            Map.entry("UR", "Urdu"),
            Map.entry("SA", "Sanskrit"),
            Map.entry("KO", "Konkani"),
            Map.entry("MN", "Manipuri"),
            Map.entry("NE", "Nepali"),
            Map.entry("BR", "Bodo"),
            Map.entry("DO", "Dogri"),
            Map.entry("MA", "Maithili"),
            Map.entry("ST", "Santhali"),
            Map.entry("SD", "Sindhi"),
            Map.entry("KS", "Kashmiri")
    );

    @Override
    public String extractTextFromPdf(byte[] pdfBytes) throws Exception {
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    @Override
    public List<Map<String, String>> splitIntoMicroModules(String content, List<String> problemAreas, Integer numberOfModules, String targetLanguageCode) throws Exception {
        // Default problem areas if none provided
        List<String> defaultProblemAreas = List.of(
                "ABSENTEEISM", "LANGUAGE_BARRIER", "SCIENCE_TLM", "CLASSROOM_MANAGEMENT",
                "PARENT_ENGAGEMENT", "MIXED_LEVEL_CLASSROOM", "ASSESSMENT_METHODS", "DIGITAL_LITERACY"
        );
        
        List<String> problemTagsToUse = (problemAreas != null && !problemAreas.isEmpty()) 
                ? problemAreas 
                : defaultProblemAreas;
        
        // Default number of modules if not provided
        int moduleCount = (numberOfModules != null && numberOfModules > 0) 
                ? numberOfModules 
                : 7; // Default to 7 (middle of 6-8 range)
        
        String problemTagsList = String.join(", ", problemTagsToUse);
        
        // Determine if we need to translate
        boolean needsTranslation = targetLanguageCode != null && !targetLanguageCode.isEmpty() && !"EN".equals(targetLanguageCode);
        String languageName = needsTranslation ? LANGUAGE_NAMES.getOrDefault(targetLanguageCode, targetLanguageCode) : "English";
        
        String prompt;
        if (needsTranslation) {
            // Split AND translate in one step
            prompt = String.format(
                    """
                    You are an expert in educational content organization and translation. 
                    
                    TASK: Analyze the following teacher training module content and split it into EXACTLY %d focused micro-modules. 
                    Each micro-module MUST be written in %s language (Language Code: %s).
                    
                    CRITICAL REQUIREMENTS:
                    1. Create EXACTLY %d micro-modules (no more, no less)
                    2. Each micro-module MUST focus on ONE specific problem area from this EXACT list: %s
                    3. Use ONLY the problem tags from the provided list - do not use any other tags
                    4. Each micro-module should be self-contained and practical
                    5. Each micro-module should have a clear, descriptive title IN %s
                    6. Each micro-module's content MUST be written in %s language using %s script/characters
                    7. Each micro-module should be 2-5 pages worth of content
                    8. DO NOT include any English text in the output - everything must be in %s
                    
                    Content to analyze and split:
                    %s
                    
                    Return ONLY a valid JSON array with this exact structure (no markdown, no explanation, no code blocks):
                    [
                      {
                        "title": "Module Title in %s",
                        "content": "Full content of the micro-module in %s...",
                        "problemTag": "ONE_OF_THE_PROVIDED_TAGS"
                      }
                    ]
                    
                    CRITICAL: 
                    - Use ONLY these problem tags: %s
                    - Create EXACTLY %d micro-modules
                    - Each module must have a different problemTag from the list
                    - ALL text (title and content) MUST be in %s language, NOT English
                    - Use proper %s script/characters for writing
                    """,
                    moduleCount, languageName, targetLanguageCode,
                    moduleCount,
                    problemTagsList,
                    languageName,
                    languageName, languageName,
                    languageName,
                    content.substring(0, Math.min(content.length(), 30000)), // Limit to avoid token limits
                    languageName, languageName,
                    problemTagsList,
                    moduleCount,
                    languageName,
                    languageName
            );
        } else {
            // Split only (English)
            prompt = String.format(
                    """
                    You are an expert in educational content organization. Analyze the following teacher training module content and split it into EXACTLY %d focused micro-modules.
                    
                    IMPORTANT REQUIREMENTS:
                    1. Create EXACTLY %d micro-modules (no more, no less)
                    2. Each micro-module MUST focus on ONE specific problem area from this EXACT list: %s
                    3. Use ONLY the problem tags from the provided list - do not use any other tags
                    4. Each micro-module should be self-contained and practical
                    5. Each micro-module should have a clear, descriptive title
                    6. Each micro-module should be 2-5 pages worth of content
                    
                    Content to analyze:
                    %s
                    
                    Return ONLY a valid JSON array with this exact structure (no markdown, no explanation, no code blocks):
                    [
                      {
                        "title": "Module Title",
                        "content": "Full content of the micro-module...",
                        "problemTag": "ONE_OF_THE_PROVIDED_TAGS"
                      }
                    ]
                    
                    CRITICAL: 
                    - Use ONLY these problem tags: %s
                    - Create EXACTLY %d micro-modules
                    - Each module must have a different problemTag from the list
                    - Do not use any problem tags outside of the provided list
                    """,
                    moduleCount,
                    moduleCount,
                    problemTagsList,
                    content.substring(0, Math.min(content.length(), 30000)), // Limit to avoid token limits
                    problemTagsList,
                    moduleCount
            );
        }

        try {
            String response = callGeminiAPI(prompt);
            List<Map<String, String>> result = parseMicroModulesResponse(response, problemTagsToUse, moduleCount);
            
            // Validate that we got the right number of modules
            if (result.size() != moduleCount) {
                log.warn("AI returned {} modules but {} were requested. Using fallback.", result.size(), moduleCount);
                return getFallbackMicroModules(problemTagsToUse, moduleCount);
            }
            
            // Validate that all problem tags are from the allowed list
            for (Map<String, String> module : result) {
                String tag = module.get("problemTag");
                if (tag == null || !problemTagsToUse.contains(tag)) {
                    log.warn("AI returned invalid problem tag: {}. Using fallback.", tag);
                    return getFallbackMicroModules(problemTagsToUse, moduleCount);
                }
            }
            
            return result;
        } catch (Exception e) {
            log.error("Error in AI module splitting: {}", e.getMessage());
            log.warn("Using fallback modules with user-specified problem areas and count");
            return getFallbackMicroModules(problemTagsToUse, moduleCount);
        }
    }

    @Override
    public String translateText(String text, String targetLanguageCode) throws Exception {
        if ("EN".equals(targetLanguageCode)) {
            log.info("Target language is English, skipping translation");
            return text; // No translation needed
        }

        String languageName = LANGUAGE_NAMES.getOrDefault(targetLanguageCode, targetLanguageCode);
        log.info("Starting translation to language: {} (code: {})", languageName, targetLanguageCode);
        log.debug("Text to translate (first 100 chars): {}", text.substring(0, Math.min(100, text.length())));
        
        String prompt = String.format(
                """
                YOU ARE A TRANSLATOR. YOUR TASK IS TO TRANSLATE THE GIVEN TEXT FROM ENGLISH TO %s.
                
                TARGET LANGUAGE: %s (Language Code: %s)
                
                CRITICAL INSTRUCTIONS - READ CAREFULLY:
                1. Translate EVERY SINGLE WORD from English to %s
                2. The output text MUST be written in %s language using %s script/characters
                3. DO NOT return English text - ONLY return the %s translation
                4. DO NOT include any English words in your response (except proper nouns if necessary)
                5. Maintain the same structure, meaning, and educational tone
                6. Use proper %s grammar and vocabulary
                7. Return ONLY the translated text - no explanations, no notes, no English, no markdown formatting
                
                EXAMPLE:
                If target language is Bengali (BN):
                - Input: "Managing Student Absenteeism"
                - Output: "ছাত্র অনুপস্থিতি পরিচালনা" (in Bengali script)
                
                NOW TRANSLATE THIS TEXT TO %s:
                
                %s
                
                TRANSLATED TEXT IN %s (ONLY THE TRANSLATION, NOTHING ELSE):
                """,
                languageName.toUpperCase(), languageName.toUpperCase(), targetLanguageCode,
                languageName,
                languageName, languageName,
                languageName,
                languageName,
                languageName.toUpperCase(),
                text.substring(0, Math.min(text.length(), 20000)), // Limit for translation
                languageName.toUpperCase()
        );

        log.info("Calling Gemini API for translation to {} (code: {})", languageName, targetLanguageCode);
        log.debug("Translation prompt length: {}", prompt.length());
        
        try {
            String result = callGeminiAPI(prompt).trim();
            log.info("Received response from Gemini API (length: {})", result.length());
            log.debug("Translation result (first 200 chars): {}", result.substring(0, Math.min(200, result.length())));
            
            // Check if we got the fallback response (translation service not configured)
            if (result.contains("translation service not configured") || 
                result.contains("Translated content") ||
                result.toLowerCase().contains("translation service")) {
                log.warn("Translation service not available, throwing exception to use original content");
                throw new Exception("Translation service not configured or unavailable");
            }
            
            // Check if result is still in English (basic check - if it's the same as input, might be an issue)
            if (result.equals(text.trim())) {
                log.warn("Translation result is identical to input - translation may have failed");
            }
            
            log.info("Successfully translated text to {} (code: {}) - Result length: {}", languageName, targetLanguageCode, result.length());
            return result;
        } catch (Exception e) {
            log.error("Translation to {} (code: {}) failed: {}", languageName, targetLanguageCode, e.getMessage(), e);
            throw new Exception("Failed to translate to " + languageName + " (" + targetLanguageCode + "): " + e.getMessage(), e);
        }
    }

    private String callGeminiAPI(String prompt) throws Exception {
        if (geminiApiKey == null || geminiApiKey.isEmpty()) {
            log.warn("Gemini API key not configured");
            throw new Exception("Gemini API key not configured");
        }

        try {
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);
            content.put("parts", List.of(part));
            requestBody.put("contents", List.of(content));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            String url = geminiApiUrl + "?key=" + geminiApiKey;
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // Check for HTTP errors
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Gemini API returned error: " + response.getStatusCode() + " - " + response.getBody());
            }

            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            
            // Check for API error in response body
            JsonNode error = jsonResponse.get("error");
            if (error != null) {
                String errorMessage = error.get("message") != null ? error.get("message").asText() : "Unknown error";
                String errorCode = error.get("code") != null ? String.valueOf(error.get("code").asInt()) : "Unknown";
                throw new RuntimeException("Gemini API error [" + errorCode + "]: " + errorMessage);
            }
            
            JsonNode candidates = jsonResponse.get("candidates");
            if (candidates != null && candidates.isArray() && candidates.size() > 0) {
                JsonNode contentNode = candidates.get(0).get("content");
                if (contentNode != null) {
                    JsonNode parts = contentNode.get("parts");
                    if (parts != null && parts.isArray() && parts.size() > 0) {
                        return parts.get(0).get("text").asText();
                    }
                }
            }

            throw new RuntimeException("Unexpected response format from Gemini API");
        } catch (Exception e) {
            log.error("Error calling Gemini API: {}", e.getMessage());
            // Don't silently fallback - let the caller handle it
            throw new Exception("Gemini API call failed: " + e.getMessage(), e);
        }
    }

    private List<Map<String, String>> parseMicroModulesResponse(String response, List<String> allowedProblemTags, int expectedCount) throws Exception {
        try {
            // Try to extract JSON from response (in case there's markdown formatting)
            String jsonStr = response.trim();
            if (jsonStr.startsWith("```json")) {
                jsonStr = jsonStr.substring(7);
            }
            if (jsonStr.startsWith("```")) {
                jsonStr = jsonStr.substring(3);
            }
            if (jsonStr.endsWith("```")) {
                jsonStr = jsonStr.substring(0, jsonStr.length() - 3);
            }
            jsonStr = jsonStr.trim();

            JsonNode jsonNode = objectMapper.readTree(jsonStr);
            if (jsonNode.isArray()) {
                List<Map<String, String>> result = new ArrayList<>();
                for (JsonNode node : jsonNode) {
                    Map<String, String> module = new HashMap<>();
                    module.put("title", node.get("title").asText());
                    module.put("content", node.get("content").asText());
                    String problemTag = node.get("problemTag").asText();
                    // Validate problem tag is in allowed list
                    if (allowedProblemTags.contains(problemTag)) {
                        module.put("problemTag", problemTag);
                    } else {
                        // Use first allowed tag if AI returned invalid one
                        log.warn("AI returned invalid problem tag: {}, using first allowed tag", problemTag);
                        module.put("problemTag", allowedProblemTags.get(0));
                    }
                    result.add(module);
                }
                return result;
            }
        } catch (Exception e) {
            log.error("Error parsing AI response: {}", e.getMessage());
            throw e; // Re-throw to trigger fallback
        }

        throw new Exception("Failed to parse AI response");
    }

    private String getFallbackResponse(String prompt) {
        // Fallback response when API is not available
        if (prompt.contains("split")) {
            // This should not be called anymore as we handle fallback in splitIntoMicroModules
            return objectMapper.valueToTree(getFallbackMicroModules(List.of("ABSENTEEISM", "LANGUAGE_BARRIER", "SCIENCE_TLM", "CLASSROOM_MANAGEMENT", "PARENT_ENGAGEMENT", "MIXED_LEVEL_CLASSROOM", "ASSESSMENT_METHODS"), 7)).toString();
        } else if (prompt.contains("Translate")) {
            return "Translated content (translation service not configured)";
        }
        return "AI service response";
    }

    private List<Map<String, String>> getFallbackMicroModules(List<String> problemTags, int count) {
        // Create fallback modules using the provided problem tags and count
        // This is a fallback when AI is not available - generates generic modules based on user-selected problem tags
        List<Map<String, String>> modules = new ArrayList<>();
        
        // Generate generic title from problem tag (convert SNAKE_CASE to Title Case)
        for (int i = 0; i < count; i++) {
            String problemTag = problemTags.get(i % problemTags.size());
            
            // Convert SNAKE_CASE to Title Case for the title
            String title = problemTag.replace("_", " ");
            title = Arrays.stream(title.split(" "))
                    .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                    .reduce((a, b) -> a + " " + b)
                    .orElse(problemTag);
            
            // Generate generic content based on problem tag
            String content = String.format(
                    "This module focuses on addressing %s. It provides practical strategies, techniques, and best practices for teachers to effectively handle challenges related to this area. The content is designed to be actionable and immediately applicable in classroom settings.",
                    title.toLowerCase()
            );
            
            Map<String, String> module = new HashMap<>();
            module.put("title", title);
            module.put("content", content);
            module.put("problemTag", problemTag);
            modules.add(module);
        }
        
        return modules;
    }
}
