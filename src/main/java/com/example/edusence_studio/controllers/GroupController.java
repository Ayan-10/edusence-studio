package com.example.edusence_studio.controllers;

import com.example.edusence_studio.dtos.CreateGroupRequest;
import com.example.edusence_studio.models.groups.Group;
import com.example.edusence_studio.services.groups.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public Group createGroup(@RequestBody CreateGroupRequest request) {
        return groupService.createGroup(request);
    }

    @PostMapping("/{groupId}/teachers/{teacherId}")
    public void addTeacher(
            @PathVariable UUID groupId,
            @PathVariable UUID teacherId
    ) {
        groupService.addTeacherToGroup(groupId, teacherId);
    }

    @DeleteMapping("/{groupId}/teachers/{teacherId}")
    public void removeTeacher(
            @PathVariable UUID groupId,
            @PathVariable UUID teacherId
    ) {
        groupService.removeTeacherFromGroup(groupId, teacherId);
    }

    @GetMapping("/teacher/{teacherId}")
    public List<Group> getGroupsForTeacher(@PathVariable UUID teacherId) {
        return groupService.getGroupsForTeacher(teacherId);
    }
}

