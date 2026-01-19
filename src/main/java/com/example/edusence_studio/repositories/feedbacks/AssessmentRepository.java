package com.example.edusence_studio.repositories.feedbacks;

import com.example.edusence_studio.models.feedbacks.Assessment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface AssessmentRepository extends JpaRepository<Assessment, UUID>, JpaSpecificationExecutor<Assessment> {
    @EntityGraph(attributePaths = {"questions", "feedbackCycle"})
    @Override
    List<Assessment> findAll();

    @EntityGraph(attributePaths = {"questions", "feedbackCycle"})
    @Override
    java.util.Optional<Assessment> findById(UUID id);

    @EntityGraph(attributePaths = {"questions", "feedbackCycle"})
    List<Assessment> findByFeedbackCycleId(UUID feedbackCycleId);
}
