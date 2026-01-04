package com.example.edusence_studio.models.modules;

import com.example.edusence_studio.enums.ProgressStatus;
import com.example.edusence_studio.models.BaseEntity;
import com.example.edusence_studio.models.users.TeacherProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"teacher_id", "micro_module_id"}
        )
)
public class TeacherMicroModuleProgress extends BaseEntity {

    @ManyToOne(optional = false)
    private TeacherProfile teacher;

    @ManyToOne(optional = false)
    private MicroModule microModule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProgressStatus status = ProgressStatus.NOT_STARTED;

    @Column(nullable = false)
    private int completionPercentage = 0;

    private Instant startedAt;
    private Instant completedAt;
}

