package com.example.edusence_studio.services.groups;

import com.example.edusence_studio.dtos.CreateGroupRequest;
import com.example.edusence_studio.models.groups.Group;
import com.example.edusence_studio.models.groups.GroupMember;
import com.example.edusence_studio.models.users.TeacherProfile;
import com.example.edusence_studio.models.users.User;
import com.example.edusence_studio.repositories.groups.GroupRepository;
import com.example.edusence_studio.repositories.groups.GroupTeacherMappingRepository;
import com.example.edusence_studio.repositories.users.TeacherProfileRepository;
import com.example.edusence_studio.repositories.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupTeacherMappingRepository mappingRepository;
    private final TeacherProfileRepository teacherRepository;
    private final UserRepository userRepository;

    @Override
    public Group createGroup(CreateGroupRequest request) {

        User creator = userRepository.findById(request.createdByUserId())
                .orElseThrow(() -> new RuntimeException("Creator not found"));

        Group group = new Group();
        group.setName(request.name());
        group.setDescription(request.description());
        group.setFocusProblemTag(request.focusProblemTag());
        group.setCreatedBy(creator);

        return groupRepository.save(group);
    }

    @Override
    public void addTeacherToGroup(UUID groupId, UUID teacherId) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        TeacherProfile teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        GroupMember mapping = new GroupMember();
        mapping.setGroup(group);
        mapping.setTeacher(teacher);

        mappingRepository.save(mapping);
    }

    @Override
    public void removeTeacherFromGroup(UUID groupId, UUID teacherId) {

        mappingRepository.findByGroupId(groupId).stream()
                .filter(m -> m.getTeacher().getId().equals(teacherId))
                .findFirst()
                .ifPresent(mappingRepository::delete);
    }

    @Override
    public List<Group> getGroupsForTeacher(UUID teacherId) {

        return mappingRepository.findByTeacherId(teacherId).stream()
                .map(GroupMember::getGroup)
                .toList();
    }
}

