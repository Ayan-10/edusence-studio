package com.example.edusence_studio.controllers;

import com.example.edusence_studio.dtos.UpdateProgressRequest;
import com.example.edusence_studio.models.modules.TeacherMicroModuleProgress;
import com.example.edusence_studio.services.feedbacks.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @PostMapping("/teacher/{teacherId}")
    public TeacherMicroModuleProgress updateProgress(
            @PathVariable UUID teacherId,
            @RequestBody UpdateProgressRequest request
    ) {
        return progressService.updateProgress(teacherId, request);
    }

    @GetMapping("/teacher/{teacherId}/course/{courseId}")
    public int getCourseProgress(
            @PathVariable UUID teacherId,
            @PathVariable UUID courseId
    ) {
        return progressService.calculateCourseProgress(teacherId, courseId);
    }

}
