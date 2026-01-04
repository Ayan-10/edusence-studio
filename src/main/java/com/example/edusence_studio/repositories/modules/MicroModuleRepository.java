package com.example.edusence_studio.repositories.modules;

import com.example.edusence_studio.models.modules.MicroModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MicroModuleRepository
        extends JpaRepository<MicroModule, UUID> {

    List<MicroModule> findByMainModuleId(UUID mainModuleId);
}

