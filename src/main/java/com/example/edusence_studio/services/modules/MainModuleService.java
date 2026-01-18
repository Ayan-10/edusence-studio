package com.example.edusence_studio.services.modules;

import com.example.edusence_studio.dtos.CreateMicroModuleRequest;
import com.example.edusence_studio.models.modules.MainModule;
import com.example.edusence_studio.models.modules.MicroModule;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MainModuleService {

    MainModule uploadMainModule(
            String title,
            String description,
            MultipartFile file,
            UUID uploadedByUserId
    );

    List<MicroModule> splitIntoMicroModules(
            UUID mainModuleId,
            List<CreateMicroModuleRequest> microModules
    );

    List<MicroModule> splitModuleWithAI(UUID mainModuleId, String languageCode);

    List<MainModule> getAllModules();

    MainModule getModuleById(UUID moduleId);

    List<MicroModule> getMicroModulesByMainModuleId(UUID mainModuleId);
}

