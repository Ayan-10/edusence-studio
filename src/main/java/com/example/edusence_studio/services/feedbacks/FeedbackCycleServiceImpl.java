package com.example.edusence_studio.services.feedbacks;

import com.example.edusence_studio.enums.FeedbackCycleStatus;
import com.example.edusence_studio.models.feedbacks.FeedbackCycle;
import com.example.edusence_studio.repositories.feedbacks.FeedbackCycleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedbackCycleServiceImpl implements FeedbackCycleService {

    private final FeedbackCycleRepository repository;

    @Override
    public FeedbackCycle createCycle(FeedbackCycle cycle) {
        cycle.setStatus(FeedbackCycleStatus.DRAFT);
        return repository.save(cycle);
    }

    @Override
    public FeedbackCycle activateCycle(UUID cycleId) {
        FeedbackCycle cycle = repository.findById(cycleId)
                .orElseThrow(() -> new RuntimeException("Cycle not found"));
        cycle.setStatus(FeedbackCycleStatus.ACTIVE);
        return repository.save(cycle);
    }

    @Override
    public List<FeedbackCycle> getAllCycles() {
        return repository.findAll();
    }

    @Override
    public List<FeedbackCycle> getActiveCycles() {
        return repository.findByStatus(FeedbackCycleStatus.ACTIVE);
    }

    @Override
    public FeedbackCycle getCycleById(UUID cycleId) {
        return repository.findById(cycleId)
                .orElseThrow(() -> new RuntimeException("Feedback cycle not found"));
    }

    @Override
    @Transactional
    public void deleteCycle(UUID cycleId) {
        FeedbackCycle cycle = repository.findById(cycleId)
                .orElseThrow(() -> new RuntimeException("Feedback cycle not found"));
        repository.delete(cycle);
    }
}

