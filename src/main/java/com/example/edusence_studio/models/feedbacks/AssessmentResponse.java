package com.example.edusence_studio.models.feedbacks;

import com.example.edusence_studio.models.BaseEntity;
import com.example.edusence_studio.models.users.TeacherProfile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AssessmentResponse extends BaseEntity {

    @ManyToOne
    private Assessment assessment;

    @ManyToOne
    private AssessmentQuestion question;

    @ManyToOne
    private TeacherProfile teacher;

    private Integer numericAnswer;

    @Column(columnDefinition = "TEXT")
    private String textAnswer;
}
