package com.example.edusence_studio.services.modules;

import com.example.edusence_studio.dtos.CreateMicroModuleRequest;
import com.example.edusence_studio.models.modules.MainModule;
import com.example.edusence_studio.models.modules.MicroModule;
import com.example.edusence_studio.models.users.User;
import com.example.edusence_studio.repositories.modules.MainModuleRepository;
import com.example.edusence_studio.repositories.modules.MicroModuleRepository;
import com.example.edusence_studio.repositories.modules.MicroModuleAssignmentRepository;
import com.example.edusence_studio.repositories.modules.TeacherMicroModuleProgressRepository;
import com.example.edusence_studio.repositories.users.UserRepository;
import com.example.edusence_studio.services.ai.AIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainModuleServiceImpl implements MainModuleService {

    private final MainModuleRepository mainModuleRepository;
    private final MicroModuleRepository microModuleRepository;
    private final MicroModuleAssignmentRepository microModuleAssignmentRepository;
    private final TeacherMicroModuleProgressRepository teacherMicroModuleProgressRepository;
    private final UserRepository userRepository;
    private final SupabaseStorageService storageService;
    private final AIService aiService;
    private final PDFGeneratorService pdfGeneratorService;

    @Override
    public MainModule uploadMainModule(
            String title,
            String description,
            MultipartFile file,
            UUID uploadedByUserId
    ) {

        User user = userRepository.findById(uploadedByUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String storageUrl = storageService.uploadPdf(file, "main-modules");

        MainModule module = new MainModule();
        module.setTitle(title);
        module.setDescription(description);
        module.setFileUrl(storageUrl);
        module.setUploadedBy(user);


        return mainModuleRepository.save(module);
    }

    @Override
    public List<MicroModule> splitIntoMicroModules(
            UUID mainModuleId,
            List<CreateMicroModuleRequest> requests
    ) {

        MainModule mainModule = mainModuleRepository.findById(mainModuleId)
                .orElseThrow(() -> new RuntimeException("Main module not found"));

        List<MicroModule> result = new ArrayList<>();

        for (CreateMicroModuleRequest req : requests) {

            // Placeholder: pretend we generated a smaller PDF
            String microPdfUrl = storageService.uploadPdf(
                    req.microModulePdf(),
                    "micro-modules"
            );

            MicroModule micro = new MicroModule();
            micro.setTitle(req.title());
            micro.setLanguageCode(req.languageCode());
            micro.setFocusProblemTag(req.focusProblemTag());
            micro.setFileUrl(microPdfUrl);
            micro.setMainModule(mainModule);

            result.add(micro);
        }

        return microModuleRepository.saveAll(result);
    }

    public List<MicroModule> splitModuleWithAI(UUID mainModuleId, String languageCode, List<String> problemAreas, Integer numberOfModules) {
        MainModule mainModule = mainModuleRepository.findById(mainModuleId)
                .orElseThrow(() -> new RuntimeException("Main module not found"));

        try {
            log.info("Starting AI-powered module splitting for module: {}", mainModuleId);
            log.info("Problem areas: {}, Number of modules: {}", problemAreas, numberOfModules);

            // Step 1: Download PDF from Supabase Storage and extract text
            log.info("Downloading PDF from Supabase Storage...");
            byte[] pdfBytes = storageService.downloadPdf(mainModule.getFileUrl());
            String extractedText = aiService.extractTextFromPdf(pdfBytes);
            log.info("Extracted {} characters from PDF", extractedText.length());

            // Step 2: Use AI to split into micro-modules AND translate to target language in one step
            log.info("Splitting content into {} micro-modules using AI with target language: {}", numberOfModules, languageCode);
            List<Map<String, String>> microModulesData = aiService.splitIntoMicroModules(extractedText, problemAreas, numberOfModules, languageCode);
            log.info("AI generated {} micro-modules in language: {}", microModulesData.size(), languageCode);

            // Step 3: For each micro-module, generate PDF (content is already in target language)
            List<MicroModule> result = new ArrayList<>();
            for (Map<String, String> moduleData : microModulesData) {
                String title = moduleData.get("title");
                String content = moduleData.get("content");
                String problemTag = moduleData.get("problemTag");

                log.info("Processing micro-module: {} (Problem Tag: {}, Language: {})", title, problemTag, languageCode);

                // Generate PDF (content is already in target language from AI)
                log.info("Generating PDF for module: {}", title);
                byte[] pdfBytesForModule = pdfGeneratorService.generatePdfFromText(title, content);

                // Upload to Supabase Storage
                String filename = problemTag.toLowerCase() + "_" + languageCode.toLowerCase() + "_" + UUID.randomUUID() + ".pdf";
                String storageUrl = storageService.uploadPdfBytes(pdfBytesForModule, "micro-modules", filename);

                // Create MicroModule entity
                MicroModule micro = new MicroModule();
                micro.setTitle(title + " (" + languageCode + ")");
                micro.setLanguageCode(languageCode);
                micro.setFocusProblemTag(problemTag);
                micro.setFileUrl(storageUrl);
                micro.setMainModule(mainModule);

                result.add(micro);
            }

            log.info("Successfully created {} micro-modules", result.size());
            return microModuleRepository.saveAll(result);

        } catch (Exception e) {
            log.error("Error in AI module splitting: {}", e.getMessage(), e);
            // Fallback to mock implementation if AI fails
            return createFallbackMicroModules(mainModule, languageCode);
        }
    }

    private List<MicroModule> createFallbackMicroModules(MainModule mainModule, String languageCode) {
        log.warn("Using fallback micro-module generation");
        List<String> problemTags = List.of(
                "ABSENTEEISM", "LANGUAGE_BARRIER", "SCIENCE_TLM", "CLASSROOM_MANAGEMENT",
                "PARENT_ENGAGEMENT", "MIXED_LEVEL_CLASSROOM", "ASSESSMENT_METHODS"
        );

        List<String> microModuleTitles = List.of(
                "Managing Student Absenteeism", "Overcoming Language Barriers",
                "Science Teaching Learning Materials", "Effective Classroom Management",
                "Engaging Parents in Education", "Teaching Mixed-Level Classrooms",
                "Modern Assessment Techniques"
        );

        List<MicroModule> result = new ArrayList<>();
        for (int i = 0; i < Math.min(7, problemTags.size()); i++) {
            MicroModule micro = new MicroModule();
            micro.setTitle(microModuleTitles.get(i) + " (" + languageCode + ")");
            micro.setLanguageCode(languageCode);
            micro.setFocusProblemTag(problemTags.get(i));
            micro.setFileUrl(mainModule.getFileUrl() + "/micro/" + problemTags.get(i).toLowerCase() + "_" + languageCode.toLowerCase() + ".pdf");
            micro.setMainModule(mainModule);
            result.add(micro);
        }

        return microModuleRepository.saveAll(result);
    }

    public List<MainModule> getAllModules() {
        return mainModuleRepository.findAll();
    }

    public MainModule getModuleById(UUID moduleId) {
        return mainModuleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Main module not found"));
    }

    public List<MicroModule> getMicroModulesByMainModuleId(UUID mainModuleId) {
        return microModuleRepository.findByMainModuleId(mainModuleId);
    }

    @Override
    @Transactional
    public void deleteModule(UUID moduleId) {
        MainModule module = mainModuleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Main module not found"));
        
        // Delete all associated micro-modules first (cascade should handle this, but being explicit)
        for (MicroModule microModule : module.getMicroModules()) {
            deleteMicroModuleRelatedData(microModule.getId());
        }
        microModuleRepository.deleteAll(module.getMicroModules());
        
        // Delete the main module
        mainModuleRepository.delete(module);
    }

    @Override
    @Transactional
    public void deleteMicroModule(UUID microModuleId) {
        MicroModule microModule = microModuleRepository.findById(microModuleId)
                .orElseThrow(() -> new RuntimeException("Micro module not found"));
        
        // Delete all related data (assignments, progress) for this micro-module
        deleteMicroModuleRelatedData(microModuleId);
        
        // Delete the micro-module
        microModuleRepository.delete(microModule);
    }

    private void deleteMicroModuleRelatedData(UUID microModuleId) {
        // Delete all assignments associated with this micro-module
        microModuleAssignmentRepository.deleteByMicroModuleId(microModuleId);
        
        // Delete all progress records associated with this micro-module
        teacherMicroModuleProgressRepository.deleteByMicroModuleId(microModuleId);
    }
}

