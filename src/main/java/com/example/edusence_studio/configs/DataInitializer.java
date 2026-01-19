package com.example.edusence_studio.configs;

import com.example.edusence_studio.models.feedbacks.ProblemTag;
import com.example.edusence_studio.repositories.feedbacks.ProblemTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProblemTagRepository problemTagRepository;

    @Override
    public void run(String... args) {
        initializeDefaultProblemTags();
    }

    private void initializeDefaultProblemTags() {
        List<String[]> defaultTags = List.of(
                new String[]{"ABSENTEEISM", "Managing student absenteeism and attendance issues"},
                new String[]{"LANGUAGE_BARRIER", "Overcoming language barriers in multilingual classrooms"},
                new String[]{"SCIENCE_TLM", "Science Teaching Learning Materials"},
                new String[]{"CLASSROOM_MANAGEMENT", "Effective classroom management and discipline"},
                new String[]{"PARENT_ENGAGEMENT", "Engaging parents in their children's education"},
                new String[]{"MIXED_LEVEL_CLASSROOM", "Teaching mixed-level classrooms with differentiated instruction"},
                new String[]{"ASSESSMENT_METHODS", "Modern assessment and evaluation techniques"},
                new String[]{"DIGITAL_LITERACY", "Digital literacy and technology integration in teaching"}
        );

        for (String[] tagData : defaultTags) {
            String code = tagData[0];
            String description = tagData[1];

            if (problemTagRepository.findByCode(code).isEmpty()) {
                ProblemTag tag = new ProblemTag();
                tag.setCode(code);
                tag.setDescription(description);
                problemTagRepository.save(tag);
                log.info("Created default problem tag: {}", code);
            }
        }
    }
}
