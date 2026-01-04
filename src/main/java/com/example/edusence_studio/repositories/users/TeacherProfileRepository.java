package com.example.edusence_studio.repositories.users;

import com.example.edusence_studio.models.users.TeacherProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface TeacherProfileRepository  extends JpaRepository<TeacherProfile, UUID>, JpaSpecificationExecutor<TeacherProfile> {
}
