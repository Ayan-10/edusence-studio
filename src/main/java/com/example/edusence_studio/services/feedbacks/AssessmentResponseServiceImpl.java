package com.example.edusence_studio.services.feedbacks;

import com.example.edusence_studio.models.feedbacks.AssessmentQuestion;
import com.example.edusence_studio.models.feedbacks.AssessmentResponse;
import com.example.edusence_studio.models.users.TeacherProfile;
import com.example.edusence_studio.repositories.feedbacks.AssessmentQuestionRepository;
import com.example.edusence_studio.repositories.feedbacks.AssessmentResponseRepository;
import com.example.edusence_studio.repositories.users.TeacherProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentResponseServiceImpl
        implements AssessmentResponseService {

    private final AssessmentResponseRepository responseRepo;
    private final TeacherProfileRepository teacherRepo;
    private final AssessmentQuestionRepository questionRepo;

    @Override
    public void submitResponse(UUID teacherId,
                               UUID questionId,
                               Integer numeric,
                               String text) {

        TeacherProfile teacher = resolveTeacherProfile(teacherId);

        AssessmentQuestion question = questionRepo.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        AssessmentResponse response = new AssessmentResponse();
        response.setTeacher(teacher);
        response.setQuestion(question);
        response.setNumericResponse(numeric);
        response.setTextResponse(text);

        responseRepo.save(response);

        // 🔮 FUTURE: publish event to analytics system
    }

    @Override
    public boolean hasTeacherCompletedAssessment(UUID teacherId, UUID assessmentId) {
        TeacherProfile teacher = resolveTeacherProfile(teacherId);
        
        // Get all questions for this assessment
        List<AssessmentQuestion> questions = questionRepo.findByAssessmentId(assessmentId);
        if (questions.isEmpty()) {
            return false;
        }
        
        // Get all responses from this teacher for this assessment
        List<AssessmentResponse> responses = responseRepo.findByTeacherIdAndAssessmentId(teacher.getId(), assessmentId);
        
        // Get set of question IDs that have been answered
        Set<UUID> answeredQuestionIds = responses.stream()
                .map(r -> r.getQuestion().getId())
                .collect(Collectors.toSet());
        
        // Check if all questions have been answered
        Set<UUID> allQuestionIds = questions.stream()
                .map(AssessmentQuestion::getId)
                .collect(Collectors.toSet());
        
        return answeredQuestionIds.containsAll(allQuestionIds);
    }

    private TeacherProfile resolveTeacherProfile(UUID teacherIdOrUserId) {
        return teacherRepo.findById(teacherIdOrUserId)
                .or(() -> teacherRepo.findByUserId(teacherIdOrUserId))
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
    }
}
