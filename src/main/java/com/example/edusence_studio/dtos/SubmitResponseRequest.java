package com.example.edusence_studio.dtos;

import java.util.UUID;

public record SubmitResponseRequest(
        UUID teacherId,
        UUID questionId,
        Integer numericResponse,
        String textResponse
) {}

