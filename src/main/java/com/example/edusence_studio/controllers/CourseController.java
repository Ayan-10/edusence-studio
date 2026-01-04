package com.example.edusence_studio.controllers;

import com.example.edusence_studio.dtos.CreateCourseRequest;
import com.example.edusence_studio.models.modules.Course;
import com.example.edusence_studio.services.feedbacks.CourseService;
import com.example.edusence_studio.services.modules.MicroModuleAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final MicroModuleAssignmentService assignmentService;

    @PostMapping
    public Course createCourse(@RequestBody CreateCourseRequest request) {
        return courseService.createCourse(request);
    }

    @PostMapping("/{courseId}/assign/teacher/{teacherId}")
    public void assignCourseToTeacher(
            @PathVariable UUID courseId,
            @PathVariable UUID teacherId
    ) {
        assignmentService.assignCourseToTeacher(courseId, teacherId);
    }

    @PostMapping("/{courseId}/assign/group/{groupId}")
    public void assignCourseToGroup(
            @PathVariable UUID courseId,
            @PathVariable UUID groupId
    ) {
        assignmentService.assignCourseToGroup(courseId, groupId);
    }
}

