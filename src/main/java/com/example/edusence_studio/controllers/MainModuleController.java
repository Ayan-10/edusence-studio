package com.example.edusence_studio.controllers;

import com.example.edusence_studio.dtos.CreateMicroModuleRequest;
import com.example.edusence_studio.models.modules.MainModule;
import com.example.edusence_studio.models.modules.MicroModule;
import com.example.edusence_studio.services.modules.MainModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/modules")
@RequiredArgsConstructor
public class MainModuleController {

    private final MainModuleService mainModuleService;

    @PostMapping("/upload")
    public MainModule uploadMainModule(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam MultipartFile file,
            @RequestParam UUID uploadedByUserId
    ) {
        return mainModuleService.uploadMainModule(
                title, description, file, uploadedByUserId
        );
    }

    @PostMapping("/{moduleId}/split")
    public List<MicroModule> splitModule(
            @PathVariable UUID moduleId,
            @RequestBody List<CreateMicroModuleRequest> requests
    ) {
        return mainModuleService.splitIntoMicroModules(moduleId, requests);
    }
}

