package com.example.edusence_studio.controllers;

import com.example.edusence_studio.models.feedbacks.FeedbackCycle;
import com.example.edusence_studio.services.feedbacks.FeedbackCycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/feedback-cycles")
@RequiredArgsConstructor
public class FeedbackCycleController {

    private final FeedbackCycleService service;

    @PostMapping
    public FeedbackCycle create(@RequestBody FeedbackCycle cycle) {
        return service.createCycle(cycle);
    }

    @PostMapping("/{id}/activate")
    public FeedbackCycle activate(@PathVariable UUID id) {
        return service.activateCycle(id);
    }

    @GetMapping
    public List<FeedbackCycle> getAllCycles() {
        return service.getAllCycles();
    }

    @GetMapping("/active")
    public List<FeedbackCycle> activeCycles() {
        return service.getActiveCycles();
    }

    @GetMapping("/{id}")
    public FeedbackCycle getCycle(@PathVariable UUID id) {
        return service.getCycleById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteCycle(@PathVariable UUID id) {
        service.deleteCycle(id);
    }
}

