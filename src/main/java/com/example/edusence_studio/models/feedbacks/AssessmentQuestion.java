package com.example.edusence_studio.models.feedbacks;

import com.example.edusence_studio.enums.QuestionType;
import com.example.edusence_studio.models.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AssessmentQuestion extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @Column(nullable = false, length = 1000)
    private String questionText;

    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    /**
     * Used for analytics (0–5, 0–10, etc.)
     */
    private Integer maxScore;

    /**
     * Simple, analytics-friendly tag
     * Examples:
     *  - ABSENTEEISM
     *  - LANGUAGE_BARRIER
     *  - SCIENCE_TLM
     */
    @Column(nullable = false)
    private String problemTag;
}
