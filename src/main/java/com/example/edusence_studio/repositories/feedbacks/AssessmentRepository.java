package com.example.edusence_studio.repositories.feedbacks;

import com.example.edusence_studio.models.feedbacks.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface AssessmentRepository extends JpaRepository<Assessment, UUID>, JpaSpecificationExecutor<Assessment> {
    List<Assessment> findByFeedbackCycleId(UUID feedbackCycleId);
}
