package com.example.edusence_studio.services.modules;

import com.example.edusence_studio.dtos.AssignMicroModuleToGroupRequest;
import com.example.edusence_studio.dtos.AssignMicroModuleToTeacherRequest;
import com.example.edusence_studio.enums.AssignmentTargetType;
import com.example.edusence_studio.enums.ProgressStatus;
import com.example.edusence_studio.models.groups.Group;
import com.example.edusence_studio.models.modules.Course;
import com.example.edusence_studio.models.modules.MicroModule;
import com.example.edusence_studio.models.modules.MicroModuleAssignment;
import com.example.edusence_studio.models.modules.TeacherMicroModuleProgress;
import com.example.edusence_studio.models.users.TeacherProfile;
import com.example.edusence_studio.repositories.feedbacks.CourseRepository;
import com.example.edusence_studio.repositories.modules.TeacherMicroModuleProgressRepository;
import com.example.edusence_studio.repositories.groups.GroupRepository;
import com.example.edusence_studio.repositories.modules.MicroModuleAssignmentRepository;
import com.example.edusence_studio.repositories.modules.MicroModuleRepository;
import com.example.edusence_studio.repositories.users.TeacherProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MicroModuleAssignmentService {

    private final MicroModuleRepository microModuleRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final GroupRepository groupRepository;
    private final MicroModuleAssignmentRepository microModuleAssignmentRepository;
    private final CourseRepository courseRepository;
    private final TeacherMicroModuleProgressRepository teacherMicroModuleProgressRepository;

    public void assignToTeacher(AssignMicroModuleToTeacherRequest request) {

        if (microModuleAssignmentRepository.existsByMicroModuleIdAndTeacherId(
                request.microModuleId(), request.teacherId())) {
            return;
        }

        MicroModule microModule = microModuleRepository.findById(request.microModuleId())
                .orElseThrow(() -> new RuntimeException("MicroModule not found"));

        TeacherProfile teacher = teacherProfileRepository.findById(request.teacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        MicroModuleAssignment assignment = new MicroModuleAssignment();
        assignment.setMicroModule(microModule);
        assignment.setTeacher(teacher);
        assignment.setTargetType(AssignmentTargetType.TEACHER);

        teacherMicroModuleProgressRepository.findByTeacherIdAndMicroModuleId(
                request.teacherId(), microModule.getId()
        ).orElseGet(() -> {
            TeacherMicroModuleProgress progress = new TeacherMicroModuleProgress();
            progress.setTeacher(teacher);
            progress.setMicroModule(microModule);
            progress.setStatus(ProgressStatus.NOT_STARTED);
            progress.setCompletionPercentage(0);
            return teacherMicroModuleProgressRepository.save(progress);
        });


        microModuleAssignmentRepository.save(assignment);
    }

    public void assignToGroup(AssignMicroModuleToGroupRequest request) {

        if (microModuleAssignmentRepository.existsByMicroModuleIdAndGroupId(
                request.microModuleId(), request.groupId())) {
            return;
        }

        MicroModule microModule = microModuleRepository.findById(request.microModuleId())
                .orElseThrow(() -> new RuntimeException("MicroModule not found"));

        Group group = groupRepository.findById(request.groupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        MicroModuleAssignment assignment = new MicroModuleAssignment();
        assignment.setMicroModule(microModule);
        assignment.setGroup(group);
        assignment.setTargetType(AssignmentTargetType.GROUP);

        microModuleAssignmentRepository.save(assignment);
    }

    public List<MicroModuleAssignment> getAssignmentsForTeacher(UUID teacherId) {
        return microModuleAssignmentRepository.findByTeacherId(teacherId);
    }

    public List<MicroModuleAssignment> getAssignmentsForGroup(UUID groupId) {
        return microModuleAssignmentRepository.findByGroupId(groupId);
    }

    public void assignCourseToTeacher(UUID courseId, UUID teacherId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        for (MicroModule microModule : course.getMicroModules()) {
            assignToTeacher(
                    new AssignMicroModuleToTeacherRequest(
                            microModule.getId(),
                            teacherId
                    )
            );
        }
    }

    public void assignCourseToGroup(UUID courseId, UUID groupId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        for (MicroModule microModule : course.getMicroModules()) {
            assignToGroup(
                    new AssignMicroModuleToGroupRequest(
                            microModule.getId(),
                            groupId
                    )
            );
        }
    }

}

