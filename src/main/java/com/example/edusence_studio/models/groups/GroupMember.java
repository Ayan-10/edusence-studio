package com.example.edusence_studio.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class GroupMember extends BaseEntity {

    @ManyToOne
    private Group group;

    @ManyToOne
    private TeacherProfile teacher;
}

