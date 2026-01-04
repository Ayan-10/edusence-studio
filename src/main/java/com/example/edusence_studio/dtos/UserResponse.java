package com.example.edusence_studio.dtos;

import com.example.edusence_studio.enums.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserResponse {

    private UUID id;
    private String name;
    private String email;
    private Role role;
}

