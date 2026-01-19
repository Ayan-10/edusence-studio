package com.example.edusence_studio.services.analytics;

import com.example.edusence_studio.models.feedbacks.AssessmentResponse;
import com.example.edusence_studio.models.groups.Group;
import com.example.edusence_studio.models.groups.GroupMember;
import com.example.edusence_studio.models.users.TeacherProfile;
import com.example.edusence_studio.repositories.feedbacks.AssessmentResponseRepository;
import com.example.edusence_studio.repositories.groups.GroupRepository;
import com.example.edusence_studio.repositories.modules.MicroModuleAssignmentRepository;
import com.example.edusence_studio.repositories.users.TeacherProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final AssessmentResponseRepository assessmentResponseRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final GroupRepository groupRepository;
    private final MicroModuleAssignmentRepository microModuleAssignmentRepository;

    @Override
    public Map<String, Object> getTeacherAnalytics(UUID teacherId) {
        TeacherProfile teacher = resolveTeacherProfile(teacherId);

        List<AssessmentResponse> responses = assessmentResponseRepository.findAll()
                .stream()
                .filter(r -> r.getTeacher().getId().equals(teacher.getId()))
                .toList();

        Map<String, Long> problemTagCounts = responses.stream()
                .filter(r -> r.getQuestion() != null && r.getQuestion().getProblemTag() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getQuestion().getProblemTag(),
                        Collectors.counting()
                ));

        double avgNumericResponse = responses.stream()
                .filter(r -> r.getNumericResponse() != null)
                .mapToInt(AssessmentResponse::getNumericResponse)
                .average()
                .orElse(0.0);

        Map<String, Object> result = new HashMap<>();
        result.put("teacherId", teacher.getId());
        result.put("teacherName", teacher.getUser().getName());
        result.put("clusterName", teacher.getClusterName());
        result.put("totalResponses", responses.size());
        result.put("problemTagCounts", problemTagCounts);
        result.put("averageNumericResponse", avgNumericResponse);
        result.put("responsesByProblemTag", groupResponsesByProblemTag(responses));

        return result;
    }

    @Override
    public Map<String, Object> getGroupAnalytics(UUID groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        Set<UUID> teacherIds = group.getTeachers().stream()
                .map(GroupMember::getTeacher)
                .map(TeacherProfile::getId)
                .collect(Collectors.toSet());

        List<AssessmentResponse> groupResponses = assessmentResponseRepository.findAll()
                .stream()
                .filter(r -> teacherIds.contains(r.getTeacher().getId()))
                .toList();

        Map<String, Long> problemTagCounts = groupResponses.stream()
                .filter(r -> r.getQuestion() != null && r.getQuestion().getProblemTag() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getQuestion().getProblemTag(),
                        Collectors.counting()
                ));

        Map<String, Long> clusterDistribution = group.getTeachers().stream()
                .map(GroupMember::getTeacher)
                .filter(t -> t.getClusterName() != null)
                .collect(Collectors.groupingBy(
                        TeacherProfile::getClusterName,
                        Collectors.counting()
                ));

        Map<String, Object> result = new HashMap<>();
        result.put("groupId", groupId);
        result.put("groupName", group.getName());
        result.put("focusProblemTag", group.getFocusProblemTag());
        result.put("totalTeachers", group.getTeachers().size());
        result.put("totalResponses", groupResponses.size());
        result.put("problemTagCounts", problemTagCounts);
        result.put("clusterDistribution", clusterDistribution);
        result.put("averageResponseByTeacher", calculateAverageByTeacher(groupResponses));

        return result;
    }

    @Override
    public Map<String, Object> getProblemTagAnalytics() {
        List<AssessmentResponse> allResponses = assessmentResponseRepository.findAll();

        Map<String, Long> problemTagCounts = allResponses.stream()
                .filter(r -> r.getQuestion() != null && r.getQuestion().getProblemTag() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getQuestion().getProblemTag(),
                        Collectors.counting()
                ));

        Map<String, Double> problemTagAverages = allResponses.stream()
                .filter(r -> r.getQuestion() != null && 
                            r.getQuestion().getProblemTag() != null && 
                            r.getNumericResponse() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getQuestion().getProblemTag(),
                        Collectors.averagingInt(AssessmentResponse::getNumericResponse)
                ));

        Map<String, Object> result = new HashMap<>();
        result.put("problemTagCounts", problemTagCounts);
        result.put("problemTagAverages", problemTagAverages);
        result.put("totalResponses", allResponses.size());
        result.put("uniqueProblemTags", problemTagCounts.keySet());

        return result;
    }

    @Override
    public Map<String, Object> getOverviewAnalytics() {
        long totalTeachers = teacherProfileRepository.count();
        long totalGroups = groupRepository.count();
        long totalResponses = assessmentResponseRepository.count();

        List<AssessmentResponse> allResponses = assessmentResponseRepository.findAll();
        Map<String, Long> problemTagCounts = allResponses.stream()
                .filter(r -> r.getQuestion() != null && r.getQuestion().getProblemTag() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getQuestion().getProblemTag(),
                        Collectors.counting()
                ));

        Map<String, Long> clusterDistribution = teacherProfileRepository.findAll().stream()
                .filter(t -> t.getClusterName() != null)
                .collect(Collectors.groupingBy(
                        TeacherProfile::getClusterName,
                        Collectors.counting()
                ));

        Map<String, Object> result = new HashMap<>();
        result.put("totalTeachers", totalTeachers);
        result.put("totalGroups", totalGroups);
        result.put("totalResponses", totalResponses);
        result.put("problemTagDistribution", problemTagCounts);
        result.put("clusterDistribution", clusterDistribution);
        result.put("topProblemTags", getTopProblemTags(problemTagCounts, 5));

        return result;
    }

    private Map<String, List<Map<String, Object>>> groupResponsesByProblemTag(List<AssessmentResponse> responses) {
        return responses.stream()
                .filter(r -> r.getQuestion() != null && r.getQuestion().getProblemTag() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getQuestion().getProblemTag(),
                        Collectors.mapping(
                                r -> {
                                    Map<String, Object> responseData = new HashMap<>();
                                    responseData.put("questionId", r.getQuestion().getId());
                                    responseData.put("questionText", r.getQuestion().getQuestionText());
                                    responseData.put("numericResponse", r.getNumericResponse());
                                    responseData.put("textResponse", r.getTextResponse());
                                    return responseData;
                                },
                                Collectors.toList()
                        )
                ));
    }

    private Map<String, Double> calculateAverageByTeacher(List<AssessmentResponse> responses) {
        return responses.stream()
                .filter(r -> r.getNumericResponse() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getTeacher().getUser().getName(),
                        Collectors.averagingInt(AssessmentResponse::getNumericResponse)
                ));
    }

    private TeacherProfile resolveTeacherProfile(UUID teacherIdOrUserId) {
        return teacherProfileRepository.findById(teacherIdOrUserId)
                .or(() -> teacherProfileRepository.findByUserId(teacherIdOrUserId))
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
    }

    private List<Map<String, Object>> getTopProblemTags(Map<String, Long> problemTagCounts, int topN) {
        return problemTagCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(topN)
                .map(entry -> {
                    Map<String, Object> tagData = new HashMap<>();
                    tagData.put("tag", entry.getKey());
                    tagData.put("count", entry.getValue());
                    return tagData;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getTopProblems(int limit, String filterType, String filterValue) {
        List<AssessmentResponse> responses;

        if (filterType == null || "all".equalsIgnoreCase(filterType)) {
            // Overall top problems
            responses = assessmentResponseRepository.findAll();
        } else if ("cluster".equalsIgnoreCase(filterType)) {
            // Filter by cluster name
            Set<UUID> teacherIds = teacherProfileRepository.findAll().stream()
                    .filter(t -> filterValue != null && filterValue.equals(t.getClusterName()))
                    .map(TeacherProfile::getId)
                    .collect(Collectors.toSet());
            responses = assessmentResponseRepository.findAll().stream()
                    .filter(r -> teacherIds.contains(r.getTeacher().getId()))
                    .toList();
        } else if ("group".equalsIgnoreCase(filterType)) {
            // Filter by group ID
            Group group = groupRepository.findById(UUID.fromString(filterValue))
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            Set<UUID> teacherIds = group.getTeachers().stream()
                    .map(GroupMember::getTeacher)
                    .map(TeacherProfile::getId)
                    .collect(Collectors.toSet());
            responses = assessmentResponseRepository.findAll().stream()
                    .filter(r -> teacherIds.contains(r.getTeacher().getId()))
                    .toList();
        } else if ("teacher".equalsIgnoreCase(filterType)) {
            // Filter by teacher ID
            TeacherProfile teacher = resolveTeacherProfile(UUID.fromString(filterValue));
            responses = assessmentResponseRepository.findAll().stream()
                    .filter(r -> r.getTeacher().getId().equals(teacher.getId()))
                    .toList();
        } else if ("language".equalsIgnoreCase(filterType)) {
            // Filter by language - find teachers with modules in that language
            Set<UUID> teacherIds = new HashSet<>();
            
            // Direct teacher assignments
            microModuleAssignmentRepository.findAll().stream()
                    .filter(a -> a.getMicroModule() != null && 
                                filterValue != null && 
                                filterValue.equals(a.getMicroModule().getLanguageCode()) &&
                                a.getTeacher() != null)
                    .forEach(a -> teacherIds.add(a.getTeacher().getId()));
            
            // Group assignments - get all teachers in groups that have modules in this language
            microModuleAssignmentRepository.findAll().stream()
                    .filter(a -> a.getMicroModule() != null && 
                                filterValue != null && 
                                filterValue.equals(a.getMicroModule().getLanguageCode()) &&
                                a.getGroup() != null)
                    .forEach(a -> {
                        a.getGroup().getTeachers().forEach(member -> {
                            if (member.getTeacher() != null && member.getTeacher().getId() != null) {
                                teacherIds.add(member.getTeacher().getId());
                            }
                        });
                    });
            
            responses = assessmentResponseRepository.findAll().stream()
                    .filter(r -> r.getTeacher() != null && teacherIds.contains(r.getTeacher().getId()))
                    .toList();
        } else {
            responses = assessmentResponseRepository.findAll();
        }

        // Count problems by tag
        Map<String, Long> problemTagCounts = responses.stream()
                .filter(r -> r.getQuestion() != null && r.getQuestion().getProblemTag() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getQuestion().getProblemTag(),
                        Collectors.counting()
                ));

        // Calculate averages for numeric responses
        Map<String, Double> problemTagAverages = responses.stream()
                .filter(r -> r.getQuestion() != null && 
                            r.getQuestion().getProblemTag() != null && 
                            r.getNumericResponse() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getQuestion().getProblemTag(),
                        Collectors.averagingInt(AssessmentResponse::getNumericResponse)
                ));

        // Get top N problems
        List<Map<String, Object>> topProblems = problemTagCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    Map<String, Object> problemData = new HashMap<>();
                    problemData.put("problemTag", entry.getKey());
                    problemData.put("count", entry.getValue());
                    problemData.put("averageScore", problemTagAverages.getOrDefault(entry.getKey(), 0.0));
                    return problemData;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("topProblems", topProblems);
        result.put("filterType", filterType);
        result.put("filterValue", filterValue);
        result.put("totalResponses", responses.size());
        return result;
    }
}
