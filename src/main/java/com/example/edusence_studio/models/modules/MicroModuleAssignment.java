package com.example.edusence_studio.models.modules;

import com.example.edusence_studio.enums.AssignmentTargetType;
import com.example.edusence_studio.models.BaseEntity;
import com.example.edusence_studio.models.groups.Group;
import com.example.edusence_studio.models.users.TeacherProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        name = "micro_module_assignments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"micro_module_id", "teacher_id"}),
                @UniqueConstraint(columnNames = {"micro_module_id", "group_id"})
        }
)
public class MicroModuleAssignment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "micro_module_id", nullable = false)
    private MicroModule microModule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private TeacherProfile teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Enumerated(EnumType.STRING)
    private AssignmentTargetType targetType;
}

