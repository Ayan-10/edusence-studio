package com.example.edusence_studio.controllers;

import com.example.edusence_studio.dtos.AssignMicroModuleToGroupRequest;
import com.example.edusence_studio.dtos.AssignMicroModuleToTeacherRequest;
import com.example.edusence_studio.models.modules.MicroModuleAssignment;
import com.example.edusence_studio.services.modules.MicroModuleAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/micro-modules/assignments")
@RequiredArgsConstructor
public class MicroModuleAssignmentController {

    private final MicroModuleAssignmentService service;

    @PostMapping("/teacher")
    public void assignToTeacher(
            @RequestBody AssignMicroModuleToTeacherRequest request
    ) {
        service.assignToTeacher(request);
    }

    @PostMapping("/group")
    public void assignToGroup(
            @RequestBody AssignMicroModuleToGroupRequest request
    ) {
        service.assignToGroup(request);
    }

    @GetMapping("/teacher/{teacherId}")
    public List<MicroModuleAssignment> getTeacherAssignments(
            @PathVariable UUID teacherId
    ) {
        return service.getAssignmentsForTeacher(teacherId);
    }

    @GetMapping("/group/{groupId}")
    public List<MicroModuleAssignment> getGroupAssignments(
            @PathVariable UUID groupId
    ) {
        return service.getAssignmentsForGroup(groupId);
    }
}

