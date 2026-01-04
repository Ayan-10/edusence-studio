package com.example.edusence_studio.repositories.groups;

import com.example.edusence_studio.models.groups.Group;
import com.example.edusence_studio.models.groups.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface GroupTeacherMappingRepository extends JpaRepository<GroupMember, UUID>, JpaSpecificationExecutor<GroupMember> {
    List<GroupMember> findByGroupId(UUID groupId);

    List<GroupMember> findByTeacherId(UUID teacherId);

}
