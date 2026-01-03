package com.example.edusence_studio.models.feedbacks;

import com.example.edusence_studio.models.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class QuestionProblemTag extends BaseEntity {

    @ManyToOne
    private AssessmentQuestion question;

    @ManyToOne
    private ProblemTag problemTag;
}
