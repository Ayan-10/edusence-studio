package com.example.edusence_studio.services.modules;

import com.example.edusence_studio.dtos.CreateMicroModuleRequest;
import com.example.edusence_studio.models.modules.MainModule;
import com.example.edusence_studio.models.modules.MicroModule;
import com.example.edusence_studio.models.users.User;
import com.example.edusence_studio.repositories.modules.MainModuleRepository;
import com.example.edusence_studio.repositories.modules.MicroModuleRepository;
import com.example.edusence_studio.repositories.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MainModuleServiceImpl implements MainModuleService {

    private final MainModuleRepository mainModuleRepository;
    private final MicroModuleRepository microModuleRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Override
    public MainModule uploadMainModule(
            String title,
            String description,
            MultipartFile file,
            UUID uploadedByUserId
    ) {

        User user = userRepository.findById(uploadedByUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String s3Url = s3Service.uploadPdf(file, "main-modules");

        MainModule module = new MainModule();
        module.setTitle(title);
        module.setDescription(description);
        module.setFileUrl(s3Url);
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
            String microPdfUrl = s3Service.uploadPdf(
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
}

