package com.example.edusence_studio.services.feedbacks;

import com.example.edusence_studio.models.feedbacks.ProblemTag;
import com.example.edusence_studio.repositories.feedbacks.ProblemTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProblemTagServiceImpl implements ProblemTagService {

    private final ProblemTagRepository problemTagRepository;

    @Override
    public List<ProblemTag> getAllProblemTags() {
        return problemTagRepository.findAllByOrderByCodeAsc();
    }

    @Override
    public ProblemTag getProblemTagById(UUID id) {
        return problemTagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem tag not found"));
    }

    @Override
    public ProblemTag getProblemTagByCode(String code) {
        return problemTagRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Problem tag not found with code: " + code));
    }

    @Override
    @Transactional
    public ProblemTag createProblemTag(String code, String description) {
        // Check if code already exists
        if (problemTagRepository.findByCode(code).isPresent()) {
            throw new RuntimeException("Problem tag with code '" + code + "' already exists");
        }

        ProblemTag problemTag = new ProblemTag();
        problemTag.setCode(code.toUpperCase());
        problemTag.setDescription(description);
        return problemTagRepository.save(problemTag);
    }

    @Override
    @Transactional
    public ProblemTag updateProblemTag(UUID id, String code, String description) {
        ProblemTag problemTag = problemTagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem tag not found"));

        // Check if code is being changed and if new code already exists
        if (!problemTag.getCode().equals(code.toUpperCase())) {
            if (problemTagRepository.findByCode(code.toUpperCase()).isPresent()) {
                throw new RuntimeException("Problem tag with code '" + code + "' already exists");
            }
        }

        problemTag.setCode(code.toUpperCase());
        problemTag.setDescription(description);
        return problemTagRepository.save(problemTag);
    }

    @Override
    @Transactional
    public void deleteProblemTag(UUID id) {
        ProblemTag problemTag = problemTagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem tag not found"));
        problemTagRepository.delete(problemTag);
    }
}
