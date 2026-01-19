package com.example.edusence_studio.repositories.modules;

import com.example.edusence_studio.models.modules.MainModule;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MainModuleRepository
        extends JpaRepository<MainModule, UUID> {
    
    @EntityGraph(attributePaths = {"uploadedBy", "microModules"})
    @Override
    List<MainModule> findAll();
}
