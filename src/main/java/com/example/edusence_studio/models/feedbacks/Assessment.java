package com.example.edusence_studio.models.feedbacks;

import com.example.edusence_studio.enums.AssignmentTargetType;
import com.example.edusence_studio.models.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Assessment extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "feedback_cycle_id", nullable = false)
    @JsonBackReference
    private FeedbackCycle feedbackCycle;

    @Enumerated(EnumType.STRING)
    private AssignmentTargetType assignmentTargetType = AssignmentTargetType.ALL;

    private UUID assignmentTargetId;

    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<AssessmentQuestion> questions = new ArrayList<>();
}

