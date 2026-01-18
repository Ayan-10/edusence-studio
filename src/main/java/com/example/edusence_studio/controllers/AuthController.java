package com.example.edusence_studio.controllers;

import com.example.edusence_studio.dtos.LoginRequest;
import com.example.edusence_studio.dtos.LoginResponse;
import com.example.edusence_studio.dtos.RegisterRequest;
import com.example.edusence_studio.dtos.UserResponse;
import com.example.edusence_studio.services.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
}
