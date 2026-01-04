package com.example.edusence_studio.dtos;

import java.util.UUID;

public record UpdateProgressRequest(
        UUID microModuleId,
        int completionPercentage
) {}
