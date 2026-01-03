package com.example.edusence_studio.utils;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class DBHealthCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
}
