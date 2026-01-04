package com.example.edusence_studio.dtos;

import java.util.UUID;

public record AssignMicroModuleToTeacherRequest(
        UUID microModuleId,
        UUID teacherId
) {}

