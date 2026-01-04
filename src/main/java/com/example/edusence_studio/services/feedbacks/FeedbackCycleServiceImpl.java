package com.example.edusence_studio.services.feedbacks;

import com.example.edusence_studio.enums.FeedbackCycleStatus;
import com.example.edusence_studio.models.feedbacks.FeedbackCycle;
import com.example.edusence_studio.repositories.feedbacks.FeedbackCycleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public List<FeedbackCycle> getActiveCycles() {
        return repository.findAll()
                .stream()
                .filter(c -> c.getStatus() == FeedbackCycleStatus.ACTIVE)
                .toList();
    }
}

