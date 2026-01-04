package com.example.edusence_studio.repositories.feedbacks;

import com.example.edusence_studio.models.modules.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
}
