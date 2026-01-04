package com.example.edusence_studio.services.feedbacks;

import com.example.edusence_studio.dtos.CreateCourseRequest;
import com.example.edusence_studio.models.modules.Course;
import com.example.edusence_studio.models.modules.MicroModule;
import com.example.edusence_studio.repositories.feedbacks.CourseRepository;
import com.example.edusence_studio.repositories.modules.MicroModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService{

    private final CourseRepository courseRepository;
    private final MicroModuleRepository microModuleRepository;

    public Course createCourse(CreateCourseRequest request) {

        Set<MicroModule> microModules =
                new HashSet<>(microModuleRepository.findAllById(request.microModuleIds()));

        Course course = new Course();
        course.setTitle(request.title());
        course.setDescription(request.description());
        course.setMicroModules(microModules);

        return courseRepository.save(course);
    }

    public Course getById(UUID courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }
}

