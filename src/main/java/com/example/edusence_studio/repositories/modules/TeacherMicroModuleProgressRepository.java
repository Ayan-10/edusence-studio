package com.example.edusence_studio.repositories.modules;

import com.example.edusence_studio.models.modules.TeacherMicroModuleProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeacherMicroModuleProgressRepository
        extends JpaRepository<TeacherMicroModuleProgress, UUID> {

    Optional<TeacherMicroModuleProgress>
    findByTeacherIdAndMicroModuleId(UUID teacherId, UUID microModuleId);

    List<TeacherMicroModuleProgress>
    findByTeacherId(UUID teacherId);

    List<TeacherMicroModuleProgress>
    findByMicroModuleId(UUID microModuleId);
}

