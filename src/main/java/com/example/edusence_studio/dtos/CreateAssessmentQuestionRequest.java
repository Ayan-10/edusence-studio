package com.example.edusence_studio.dtos;

import com.example.edusence_studio.enums.QuestionType;

public record CreateAssessmentQuestionRequest(
        String questionText,
        QuestionType questionType,
        Integer maxScore,
        String problemTag
) {}
