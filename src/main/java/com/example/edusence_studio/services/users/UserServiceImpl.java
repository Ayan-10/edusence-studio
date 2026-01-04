package com.example.edusence_studio.services.users;

import com.example.edusence_studio.dtos.CreateUserRequest;
import com.example.edusence_studio.dtos.UserResponse;
import com.example.edusence_studio.enums.Role;
import com.example.edusence_studio.models.users.TeacherProfile;
import com.example.edusence_studio.models.users.TrainingProfessionalProfile;
import com.example.edusence_studio.models.users.User;
import com.example.edusence_studio.repositories.users.TeacherProfileRepository;
import com.example.edusence_studio.repositories.users.TrainingProfessionalProfileRepository;
import com.example.edusence_studio.repositories.users.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final TrainingProfessionalProfileRepository trainingProfileRepository;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) throws BadRequestException {

        validateCreationRights(request);

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setPasswordHash("TEMP"); // replaced later

        user = userRepository.save(user);

        if (request.getRole() == Role.TEACHER) {
            createTeacherProfile(user, request);
        }

        if (request.getRole() == Role.TRAINING_PROFESSIONAL) {
            createTrainingProfessionalProfile(user, request);
        }

        return mapToResponse(user);
    }

    private void validateCreationRights(CreateUserRequest request) throws BadRequestException {
        if (request.getRole() == Role.TRAINING_PROFESSIONAL &&
                request.getCreatedByRole() != Role.ADMIN) {
            throw new BadRequestException(
                    "Only ADMIN can create Training Professionals");
        }
    }

    private void createTeacherProfile(User user, CreateUserRequest request) {
        TeacherProfile profile = new TeacherProfile();
        profile.setUser(user);
        profile.setSchoolName(request.getSchoolName());
        profile.setClusterName(request.getClusterName());
        profile.setSubject(request.getSubject());
        teacherProfileRepository.save(profile);
    }

    private void createTrainingProfessionalProfile(User user, CreateUserRequest request) {
        TrainingProfessionalProfile profile = new TrainingProfessionalProfile();
        profile.setUser(user);
        profile.setDesignation(request.getDesignation());
        trainingProfileRepository.save(profile);
    }

    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        return response;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(user);
    }
}

