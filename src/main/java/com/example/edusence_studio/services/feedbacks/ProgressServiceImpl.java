package com.example.edusence_studio.services.feedbacks;

import com.example.edusence_studio.dtos.UpdateProgressRequest;
import com.example.edusence_studio.enums.ProgressStatus;
import com.example.edusence_studio.models.modules.Course;
import com.example.edusence_studio.models.modules.MicroModule;
import com.example.edusence_studio.models.modules.TeacherMicroModuleProgress;
import com.example.edusence_studio.repositories.feedbacks.CourseRepository;
import com.example.edusence_studio.repositories.modules.TeacherMicroModuleProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressServiceImpl implements ProgressService{

    private final TeacherMicroModuleProgressRepository teacherMicroModuleProgressRepository;
    private final CourseRepository courseRepository;

    public TeacherMicroModuleProgress updateProgress(
            UUID teacherId,
            UpdateProgressRequest request
    ) {

        TeacherMicroModuleProgress progress =
                teacherMicroModuleProgressRepository.findByTeacherIdAndMicroModuleId(
                        teacherId,
                        request.microModuleId()
                ).orElseThrow(() ->
                        new RuntimeException("MicroModule not assigned")
                );

        int percentage = request.completionPercentage();
        progress.setCompletionPercentage(percentage);

        if (percentage > 0 && progress.getStartedAt() == null) {
            progress.setStartedAt(Instant.now());
            progress.setStatus(ProgressStatus.IN_PROGRESS);
        }

        if (percentage >= 100) {
            progress.setCompletionPercentage(100);
            progress.setStatus(ProgressStatus.COMPLETED);
            progress.setCompletedAt(Instant.now());
        }

        return teacherMicroModuleProgressRepository.save(progress);
    }

    public int calculateCourseProgress(UUID teacherId, UUID courseId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        List<TeacherMicroModuleProgress> progresses =
                teacherMicroModuleProgressRepository.findByTeacherId(teacherId);

        Map<UUID, TeacherMicroModuleProgress> progressMap =
                progresses.stream()
                        .collect(Collectors.toMap(
                                p -> p.getMicroModule().getId(),
                                p -> p
                        ));

        int total = course.getMicroModules().size();
        int completed = 0;

        for (MicroModule mm : course.getMicroModules()) {
            TeacherMicroModuleProgress p = progressMap.get(mm.getId());
            if (p != null && p.getStatus() == ProgressStatus.COMPLETED) {
                completed++;
            }
        }

        return total == 0 ? 0 : (completed * 100) / total;
    }

}

