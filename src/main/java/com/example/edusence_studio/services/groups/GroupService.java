package com.example.edusence_studio.services.groups;

import com.example.edusence_studio.dtos.CreateGroupRequest;
import com.example.edusence_studio.models.groups.Group;

import java.util.List;
import java.util.UUID;

public interface GroupService {

    Group createGroup(CreateGroupRequest request);

    void addTeacherToGroup(UUID groupId, UUID teacherId);

    void removeTeacherFromGroup(UUID groupId, UUID teacherId);

    List<Group> getAllGroups();

    List<Group> getGroupsForTeacher(UUID teacherId);
}

