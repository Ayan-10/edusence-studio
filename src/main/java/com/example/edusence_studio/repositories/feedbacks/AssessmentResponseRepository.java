package com.example.edusence_studio.repositories.feedbacks;

import com.example.edusence_studio.models.feedbacks.AssessmentResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface AssessmentResponseRepository  extends JpaRepository<AssessmentResponse, UUID>, JpaSpecificationExecutor<AssessmentResponse> {
}
