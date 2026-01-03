package com.example.edusence_studio.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "groups")
public class Group extends BaseEntity {

    private String name;
    private String description;

    @ManyToOne
    private User createdBy;
}
