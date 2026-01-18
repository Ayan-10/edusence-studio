package com.example.edusence_studio.services.auth;

import com.example.edusence_studio.dtos.LoginRequest;
import com.example.edusence_studio.dtos.LoginResponse;
import com.example.edusence_studio.dtos.RegisterRequest;
import com.example.edusence_studio.dtos.UserResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    UserResponse register(RegisterRequest request);
}
