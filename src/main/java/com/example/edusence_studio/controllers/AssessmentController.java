package com.example.edusence_studio.controllers;

import com.example.edusence_studio.dtos.CreateAssessmentRequest;
import com.example.edusence_studio.enums.AssignmentTargetType;
import com.example.edusence_studio.models.feedbacks.Assessment;
import com.example.edusence_studio.models.feedbacks.AssessmentQuestion;
import com.example.edusence_studio.models.feedbacks.FeedbackCycle;
import com.example.edusence_studio.repositories.feedbacks.AssessmentQuestionRepository;
import com.example.edusence_studio.repositories.feedbacks.AssessmentRepository;
import com.example.edusence_studio.repositories.feedbacks.FeedbackCycleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/assessments")
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentRepository assessmentRepository;
    private final AssessmentQuestionRepository questionRepository;
    private final FeedbackCycleRepository feedbackCycleRepository;

    @PostMapping
    public ResponseEntity<Assessment> createAssessment(
            @RequestBody CreateAssessmentRequest request
    ) {
        FeedbackCycle cycle = feedbackCycleRepository.findById(request.feedbackCycleId())
                .orElseThrow(() -> new RuntimeException("Feedback cycle not found"));

        Assessment assessment = new Assessment();
        assessment.setTitle(request.title());
        assessment.setFeedbackCycle(cycle);

        AssignmentTargetType targetType =
                request.assignmentTargetType() == null
                        ? AssignmentTargetType.ALL
                        : request.assignmentTargetType();
        assessment.setAssignmentTargetType(targetType);
        assessment.setAssignmentTargetId(request.assignmentTargetId());
        if (targetType != AssignmentTargetType.ALL && request.assignmentTargetId() == null) {
            throw new RuntimeException("Assignment target id is required");
        }

        List<AssessmentQuestion> questions = new ArrayList<>();
        if (request.questions() != null) {
            for (var questionRequest : request.questions()) {
                AssessmentQuestion question = new AssessmentQuestion();
                question.setAssessment(assessment);
                question.setQuestionText(questionRequest.questionText());
                question.setQuestionType(questionRequest.questionType());
                question.setMaxScore(questionRequest.maxScore());
                question.setProblemTag(questionRequest.problemTag());
                questions.add(question);
            }
        }
        assessment.setQuestions(questions);

        Assessment saved = assessmentRepository.save(assessment);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Assessment>> getAllAssessments() {
        return ResponseEntity.ok(assessmentRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Assessment> getAssessment(@PathVariable UUID id) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));
        return ResponseEntity.ok(assessment);
    }

    @GetMapping("/{id}/questions")
    public ResponseEntity<List<AssessmentQuestion>> getAssessmentQuestions(@PathVariable UUID id) {
        List<AssessmentQuestion> questions = questionRepository.findAll()
                .stream()
                .filter(q -> q.getAssessment().getId().equals(id))
                .toList();
        return ResponseEntity.ok(questions);
    }
}
