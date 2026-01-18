package com.example.edusence_studio.models.feedbacks;

import com.example.edusence_studio.enums.AssignmentTargetType;
import com.example.edusence_studio.models.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Assessment extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "feedback_cycle_id", nullable = false)
    private FeedbackCycle feedbackCycle;

    @Enumerated(EnumType.STRING)
    private AssignmentTargetType assignmentTargetType = AssignmentTargetType.ALL;

    private UUID assignmentTargetId;

    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL)
    private List<AssessmentQuestion> questions = new ArrayList<>();
}

