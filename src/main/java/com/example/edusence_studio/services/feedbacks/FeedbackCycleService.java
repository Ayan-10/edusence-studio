package com.example.edusence_studio.services.feedbacks;

import com.example.edusence_studio.models.feedbacks.FeedbackCycle;

import java.util.List;
import java.util.UUID;

public interface FeedbackCycleService {

    FeedbackCycle createCycle(FeedbackCycle cycle);

    FeedbackCycle activateCycle(UUID cycleId);

    List<FeedbackCycle> getActiveCycles();
}
