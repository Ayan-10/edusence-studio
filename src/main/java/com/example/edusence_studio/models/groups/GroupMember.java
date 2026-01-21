package com.example.edusence_studio.models.groups;

import com.example.edusence_studio.models.BaseEntity;
import com.example.edusence_studio.models.users.TeacherProfile;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        name = "group_teacher_mappings",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"group_id", "teacher_id"}
        )
)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class GroupMember extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @JsonBackReference
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private TeacherProfile teacher;
}

