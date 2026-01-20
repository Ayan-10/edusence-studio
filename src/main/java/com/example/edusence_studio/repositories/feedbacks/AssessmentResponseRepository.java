package com.example.edusence_studio.repositories.feedbacks;

import com.example.edusence_studio.models.feedbacks.AssessmentResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AssessmentResponseRepository  extends JpaRepository<AssessmentResponse, UUID>, JpaSpecificationExecutor<AssessmentResponse> {
    List<AssessmentResponse> findByTeacherId(UUID teacherId);
    
    @Query("SELECT ar FROM AssessmentResponse ar WHERE ar.teacher.id = :teacherId AND ar.question.assessment.id = :assessmentId")
    List<AssessmentResponse> findByTeacherIdAndAssessmentId(@Param("teacherId") UUID teacherId, @Param("assessmentId") UUID assessmentId);
    
    boolean existsByTeacherIdAndQuestionId(UUID teacherId, UUID questionId);
}
