package com.example.edusence_studio.models.modules;

import com.example.edusence_studio.models.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MicroModule extends BaseEntity {

    @ManyToOne
    private MainModule mainModule;

    private String title;
    private String competencyTag;
    private String language;
    private Integer estimatedMinutes;

    @Column(columnDefinition = "TEXT")
    private String content;
}

