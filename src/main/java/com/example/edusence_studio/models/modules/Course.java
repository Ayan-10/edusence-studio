package com.example.edusence_studio.models.modules;

import com.example.edusence_studio.models.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Course extends BaseEntity {

    private String name;

    @ManyToMany
    private List<MicroModule> microModules;
}

