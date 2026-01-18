package com.example.edusence_studio.dtos;

import com.example.edusence_studio.enums.AssignmentTargetType;

import java.util.List;
import java.util.UUID;

public record CreateAssessmentRequest(
        String title,
        UUID feedbackCycleId,
        AssignmentTargetType assignmentTargetType,
        UUID assignmentTargetId,
        List<CreateAssessmentQuestionRequest> questions
) {}
