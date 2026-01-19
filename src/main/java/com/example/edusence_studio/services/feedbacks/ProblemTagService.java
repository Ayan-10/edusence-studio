package com.example.edusence_studio.services.feedbacks;

import com.example.edusence_studio.models.feedbacks.ProblemTag;

import java.util.List;
import java.util.UUID;

public interface ProblemTagService {
    List<ProblemTag> getAllProblemTags();
    ProblemTag getProblemTagById(UUID id);
    ProblemTag getProblemTagByCode(String code);
    ProblemTag createProblemTag(String code, String description);
    ProblemTag updateProblemTag(UUID id, String code, String description);
    void deleteProblemTag(UUID id);
}
