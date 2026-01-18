package com.example.edusence_studio.services.feedbacks;

import com.example.edusence_studio.dtos.CreateCourseRequest;
import com.example.edusence_studio.models.modules.Course;
import java.util.UUID;


public interface CourseService {

    Course createCourse(CreateCourseRequest request);

    java.util.List<Course> getAllCourses();

    Course getById(UUID courseId);
}

