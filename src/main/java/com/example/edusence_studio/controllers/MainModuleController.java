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

    @PostMapping("/{moduleId}/split-ai")
    public List<MicroModule> splitModuleWithAI(
            @PathVariable UUID moduleId,
            @RequestParam(defaultValue = "EN") String languageCode,
            @RequestParam(required = false) List<String> problemAreas,
            @RequestParam(required = false) Integer numberOfModules
    ) {
        System.out.println("=== SPLIT MODULE WITH AI ===");
        System.out.println("Module ID: " + moduleId);
        System.out.println("Language Code: " + languageCode);
        System.out.println("Problem Areas: " + problemAreas);
        System.out.println("Number of Modules: " + numberOfModules);
        return mainModuleService.splitModuleWithAI(moduleId, languageCode, problemAreas, numberOfModules);
    }

    @GetMapping
    public List<MainModule> getAllModules() {
        return mainModuleService.getAllModules();
    }

    @GetMapping("/{id}")
    public MainModule getModule(@PathVariable UUID id) {
        return mainModuleService.getModuleById(id);
    }

    @GetMapping("/{id}/micro-modules")
    public List<MicroModule> getMicroModules(@PathVariable UUID id) {
        return mainModuleService.getMicroModulesByMainModuleId(id);
    }

    @DeleteMapping("/{id}")
    public void deleteModule(@PathVariable UUID id) {
        mainModuleService.deleteModule(id);
    }

    @DeleteMapping("/micro-modules/{microModuleId}")
    public void deleteMicroModule(@PathVariable UUID microModuleId) {
        mainModuleService.deleteMicroModule(microModuleId);
    }
}

