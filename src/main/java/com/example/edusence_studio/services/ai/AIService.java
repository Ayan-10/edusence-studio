package com.example.edusence_studio.services.ai;

import java.util.List;
import java.util.Map;

public interface AIService {
    /**
     * Extract text from PDF file
     */
    String extractTextFromPdf(byte[] pdfBytes) throws Exception;

    /**
     * Split content into micro-modules using AI
     * Returns list of maps with: title, content, problemTag
     * @param content The content to split
     * @param problemAreas Optional list of problem area codes to focus on (e.g., ["ABSENTEEISM", "LANGUAGE_BARRIER"])
     * @param numberOfModules Optional number of micro-modules to create (default: 6-8)
     * @param targetLanguageCode Optional target language code (e.g., "BN", "HI"). If provided, content will be split and translated to this language in one step.
     */
    List<Map<String, String>> splitIntoMicroModules(String content, List<String> problemAreas, Integer numberOfModules, String targetLanguageCode) throws Exception;

    /**
     * Translate text to target language
     */
    String translateText(String text, String targetLanguageCode) throws Exception;
}
