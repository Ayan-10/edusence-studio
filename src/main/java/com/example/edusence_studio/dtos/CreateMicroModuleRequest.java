package com.example.edusence_studio.dtos;

import org.springframework.web.multipart.MultipartFile;

public record CreateMicroModuleRequest(
        String title,
        String languageCode,
        String focusProblemTag,
        MultipartFile microModulePdf
) {}

