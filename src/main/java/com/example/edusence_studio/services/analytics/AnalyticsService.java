package com.example.edusence_studio.services.analytics;

import java.util.Map;
import java.util.UUID;

public interface AnalyticsService {
    Map<String, Object> getTeacherAnalytics(UUID teacherId);
    Map<String, Object> getGroupAnalytics(UUID groupId);
    Map<String, Object> getProblemTagAnalytics();
    Map<String, Object> getOverviewAnalytics();
    Map<String, Object> getTopProblems(int limit, String filterType, String filterValue);
}
