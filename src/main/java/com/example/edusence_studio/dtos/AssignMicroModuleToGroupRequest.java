package com.example.edusence_studio.dtos;

import java.util.UUID;

public record AssignMicroModuleToGroupRequest(
        UUID microModuleId,
        UUID groupId
) {}
