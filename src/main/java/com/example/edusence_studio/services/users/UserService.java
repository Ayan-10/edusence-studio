package com.example.edusence_studio.services.users;

import com.example.edusence_studio.dtos.CreateUserRequest;
import com.example.edusence_studio.dtos.UserResponse;
import com.example.edusence_studio.exceptions.BadRequestException;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse createUser(CreateUserRequest request) throws BadRequestException;

    List<UserResponse> getAllUsers();

    UserResponse getUserById(UUID userId);
}

