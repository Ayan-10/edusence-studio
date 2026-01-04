package com.example.edusence_studio.dtos;

import java.util.Set;
import java.util.UUID;

public record CreateCourseRequest(
        String title,
        String description,
        Set<UUID> microModuleIds
) {}
