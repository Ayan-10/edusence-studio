package com.example.edusence_studio.models.users;

import com.example.edusence_studio.models.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TrainingProfessionalProfile extends BaseEntity {

    @OneToOne
    @JoinColumn(nullable = false)
    private User user;

    private String designation;
}

