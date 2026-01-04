package com.example.edusence_studio.controllers;

import com.example.edusence_studio.dtos.SubmitResponseRequest;
import com.example.edusence_studio.services.feedbacks.AssessmentResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

