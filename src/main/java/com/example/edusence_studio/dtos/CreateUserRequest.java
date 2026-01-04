package com.example.edusence_studio.dtos;

import com.example.edusence_studio.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {

    private String name;
    private String email;

    private Role role;
    private Role createdByRole;

    private String schoolName;
    private String clusterName;
    private String subject;

    private String designation;
}
