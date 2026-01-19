package com.example.edusence_studio.models.feedbacks;

import com.example.edusence_studio.enums.FeedbackCycleStatus;
import com.example.edusence_studio.models.BaseEntity;
import com.example.edusence_studio.models.users.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FeedbackCycle extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private FeedbackCycleStatus status;

    @OneToMany(mappedBy = "feedbackCycle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Assessment> assessments = new ArrayList<>();
}
