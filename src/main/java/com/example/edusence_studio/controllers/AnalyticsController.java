package com.example.edusence_studio.controllers;

import com.example.edusence_studio.services.analytics.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/teachers/{teacherId}")
    public ResponseEntity<Map<String, Object>> getTeacherAnalytics(@PathVariable UUID teacherId) {
        return ResponseEntity.ok(analyticsService.getTeacherAnalytics(teacherId));
    }

    @GetMapping("/groups/{groupId}")
    public ResponseEntity<Map<String, Object>> getGroupAnalytics(@PathVariable UUID groupId) {
        return ResponseEntity.ok(analyticsService.getGroupAnalytics(groupId));
    }

    @GetMapping("/problems")
    public ResponseEntity<Map<String, Object>> getProblemTagAnalytics() {
        return ResponseEntity.ok(analyticsService.getProblemTagAnalytics());
    }

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverviewAnalytics() {
        return ResponseEntity.ok(analyticsService.getOverviewAnalytics());
    }

    @GetMapping("/top-problems")
    public ResponseEntity<Map<String, Object>> getTopProblems(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String filterType,
            @RequestParam(required = false) String filterValue
    ) {
        return ResponseEntity.ok(analyticsService.getTopProblems(limit, filterType, filterValue));
    }
}
