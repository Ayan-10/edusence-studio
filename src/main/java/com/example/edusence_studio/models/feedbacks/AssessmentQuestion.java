package com.example.edusence_studio.models.feedbacks;

import com.example.edusence_studio.enums.QuestionType;
import com.example.edusence_studio.models.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AssessmentQuestion extends BaseEntity {

    @ManyToOne
    private Assessment assessment;

    private String questionText;

    @Enumerated(EnumType.STRING)
    private QuestionType questionType;
}
