package com.example.edusence_studio.controllers;

import com.example.edusence_studio.dtos.SubmitResponseRequest;
import com.example.edusence_studio.services.feedbacks.AssessmentResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/assessment-responses")
@RequiredArgsConstructor
public class AssessmentResponseController {

    private final AssessmentResponseService service;

    @PostMapping
    public void submit(@RequestBody SubmitResponseRequest request) {
        service.submitResponse(
                request.teacherId(),
                request.questionId(),
                request.numericResponse(),
                request.textResponse()
        );
    }

    @GetMapping("/teacher/{teacherId}/assessment/{assessmentId}/completed")
    public Map<String, Boolean> checkCompletion(
            @PathVariable UUID teacherId,
            @PathVariable UUID assessmentId
    ) {
        boolean completed = service.hasTeacherCompletedAssessment(teacherId, assessmentId);
        return Map.of("completed", completed);
    }
}

