package com.example.edusence_studio.services.feedbacks;

import java.util.UUID;

public interface AssessmentResponseService {

    void submitResponse(UUID teacherId, UUID questionId,
                        Integer numeric, String text);
}
