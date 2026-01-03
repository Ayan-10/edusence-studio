package com.example.edusence_studio.models.groups;

import com.example.edusence_studio.models.BaseEntity;
import com.example.edusence_studio.models.users.TeacherProfile;
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

