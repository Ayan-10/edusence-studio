package com.example.edusence_studio.repositories.groups;

import com.example.edusence_studio.models.feedbacks.AssessmentQuestion;
import com.example.edusence_studio.models.groups.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, UUID>, JpaSpecificationExecutor<Group> {
}
