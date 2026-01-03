package com.example.edusence_studio.models.feedbacks;

import com.example.edusence_studio.models.BaseEntity;
import com.example.edusence_studio.models.users.User;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class FeedbackCycle extends BaseEntity {

    private String title;

    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne
    private User createdBy;
}
