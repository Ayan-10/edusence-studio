package com.example.edusence_studio.services.auth;

import com.example.edusence_studio.dtos.*;
import com.example.edusence_studio.enums.Role;
import com.example.edusence_studio.models.users.TeacherProfile;
import com.example.edusence_studio.models.users.TrainingProfessionalProfile;
import com.example.edusence_studio.models.users.User;
import com.example.edusence_studio.repositories.users.TeacherProfileRepository;
import com.example.edusence_studio.repositories.users.TrainingProfessionalProfileRepository;
import com.example.edusence_studio.repositories.users.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final TrainingProfessionalProfileRepository trainingProfileRepository;

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Placeholder: In real implementation, verify password hash
        // For now, accept any password if user exists
        String token = generateMockToken(user.getId(), user.getEmail(), user.getRole());
        
        return new LoginResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        // Placeholder: In real implementation, hash the password
        user.setPasswordHash("HASHED_" + request.getPassword());
        user.setPreferredLanguage("EN"); // Default

        user = userRepository.save(user);

        // Create profile based on role
        if (request.getRole() == Role.TEACHER) {
            TeacherProfile profile = new TeacherProfile();
            profile.setUser(user);
            profile.setSchoolName(request.getSchoolName());
            profile.setClusterName(request.getClusterName());
            profile.setSubject(request.getSubject());
            teacherProfileRepository.save(profile);
        } else if (request.getRole() == Role.TRAINING_PROFESSIONAL) {
            TrainingProfessionalProfile profile = new TrainingProfessionalProfile();
            profile.setUser(user);
            profile.setDesignation(request.getDesignation());
            trainingProfileRepository.save(profile);
        }

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        return response;
    }

    private String generateMockToken(UUID userId, String email, Role role) {
        // Placeholder: Generate a simple mock token
        // Format: MOCK_TOKEN_{userId}_{email}_{role}
        return "MOCK_TOKEN_" + userId.toString() + "_" + email + "_" + role.name();
    }
}
