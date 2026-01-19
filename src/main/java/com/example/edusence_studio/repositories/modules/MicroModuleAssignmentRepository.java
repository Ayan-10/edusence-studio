package com.example.edusence_studio.repositories.modules;

import com.example.edusence_studio.models.modules.MicroModuleAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MicroModuleAssignmentRepository
        extends JpaRepository<MicroModuleAssignment, UUID> {

    List<MicroModuleAssignment> findByTeacherId(UUID teacherId);

    List<MicroModuleAssignment> findByGroupId(UUID groupId);

    boolean existsByMicroModuleIdAndTeacherId(UUID microModuleId, UUID teacherId);

    boolean existsByMicroModuleIdAndGroupId(UUID microModuleId, UUID groupId);

    List<MicroModuleAssignment> findByMicroModuleId(UUID microModuleId);

    @Modifying
    @Query("DELETE FROM MicroModuleAssignment m WHERE m.microModule.id = :microModuleId")
    void deleteByMicroModuleId(@Param("microModuleId") UUID microModuleId);
}
