package com.example.edusence_studio.repositories.modules;

import com.example.edusence_studio.models.modules.MainModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MainModuleRepository
        extends JpaRepository<MainModule, UUID> {
}
