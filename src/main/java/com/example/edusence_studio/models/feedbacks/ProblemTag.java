package com.example.edusence_studio.models.feedbacks;

import com.example.edusence_studio.models.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ProblemTag extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String code;

    private String description;
}

