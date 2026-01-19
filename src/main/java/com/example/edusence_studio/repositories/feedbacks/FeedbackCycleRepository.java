package com.example.edusence_studio.repositories.feedbacks;

import com.example.edusence_studio.enums.FeedbackCycleStatus;
import com.example.edusence_studio.models.feedbacks.FeedbackCycle;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface FeedbackCycleRepository extends JpaRepository<FeedbackCycle, UUID>, JpaSpecificationExecutor<FeedbackCycle> {
    @EntityGraph(attributePaths = {"assessments"})
    @Override
    List<FeedbackCycle> findAll();

    @EntityGraph(attributePaths = {"assessments"})
    List<FeedbackCycle> findByStatus(FeedbackCycleStatus status);
}
