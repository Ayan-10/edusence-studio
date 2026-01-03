package com.example.edusence_studio.models.modules;

import com.example.edusence_studio.models.BaseEntity;
import com.example.edusence_studio.models.users.User;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MainModule extends BaseEntity {

    private String title;
    private String description;

    @ManyToOne
    private User uploadedBy;
}

