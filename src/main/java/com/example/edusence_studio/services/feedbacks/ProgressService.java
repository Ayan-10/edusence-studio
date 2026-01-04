package com.example.edusence_studio.services.feedbacks;

import com.example.edusence_studio.dtos.UpdateProgressRequest;
import com.example.edusence_studio.enums.ProgressStatus;
import com.example.edusence_studio.models.modules.Course;
import com.example.edusence_studio.models.modules.MicroModule;
import com.example.edusence_studio.models.modules.TeacherMicroModuleProgress;
import com.example.edusence_studio.repositories.modules.TeacherMicroModuleProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public interface ProgressService {

    TeacherMicroModuleProgress updateProgress(
            UUID teacherId,
            UpdateProgressRequest request
    );

    int calculateCourseProgress(UUID teacherId, UUID courseId);
}

