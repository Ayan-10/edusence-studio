package com.example.edusence_studio.controllers;

import com.example.edusence_studio.models.feedbacks.ProblemTag;
import com.example.edusence_studio.services.feedbacks.ProblemTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/problem-tags")
@RequiredArgsConstructor
public class ProblemTagController {

    private final ProblemTagService problemTagService;

    @GetMapping
    public List<ProblemTag> getAllProblemTags() {
        return problemTagService.getAllProblemTags();
    }

    @GetMapping("/{id}")
    public ProblemTag getProblemTag(@PathVariable UUID id) {
        return problemTagService.getProblemTagById(id);
    }

    @PostMapping
    public ProblemTag createProblemTag(
            @RequestParam String code,
            @RequestParam(required = false) String description
    ) {
        return problemTagService.createProblemTag(code, description);
    }

    @PutMapping("/{id}")
    public ProblemTag updateProblemTag(
            @PathVariable UUID id,
            @RequestParam String code,
            @RequestParam(required = false) String description
    ) {
        return problemTagService.updateProblemTag(id, code, description);
    }

    @DeleteMapping("/{id}")
    public void deleteProblemTag(@PathVariable UUID id) {
        problemTagService.deleteProblemTag(id);
    }
}
