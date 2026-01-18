package com.example.edusence_studio.dtos;

import com.example.edusence_studio.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private UUID userId;
    private String name;
    private String email;
    private Role role;
}
