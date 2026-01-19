package com.example.edusence_studio.repositories.feedbacks;

import com.example.edusence_studio.models.feedbacks.AssessmentQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface AssessmentQuestionRepository extends JpaRepository<AssessmentQuestion, UUID>, JpaSpecificationExecutor<AssessmentQuestion> {
    List<AssessmentQuestion> findByAssessmentId(UUID assessmentId);
}
