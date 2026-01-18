package com.example.edusence_studio.dtos;

import com.example.edusence_studio.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Role role;
    
    // For teachers
    private String schoolName;
    private String clusterName;
    private String subject;
    
    // For training professionals
    private String designation;
}
