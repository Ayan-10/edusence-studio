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


public interface CourseService {

    Course createCourse(CreateCourseRequest request);

    Course getById(UUID courseId);
}

