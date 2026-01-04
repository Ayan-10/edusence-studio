package com.example.edusence_studio.repositories.feedbacks;

import com.example.edusence_studio.models.feedbacks.FeedbackCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface FeedbackCycleRepository extends JpaRepository<FeedbackCycle, UUID>, JpaSpecificationExecutor<FeedbackCycle> {
}
