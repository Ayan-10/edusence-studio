package com.example.edusence_studio.dtos;

import java.util.UUID;

public record CreateGroupRequest(
        String name,
        String description,
        String focusProblemTag,
        UUID createdByUserId
) {}
