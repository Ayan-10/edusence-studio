package com.example.edusence_studio.models.feedbacks;

import com.example.edusence_studio.models.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Assessment extends BaseEntity {

    @ManyToOne
    private FeedbackCycle feedbackCycle;

    private String title;
}
