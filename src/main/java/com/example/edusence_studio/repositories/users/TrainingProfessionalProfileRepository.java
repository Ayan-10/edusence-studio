package com.example.edusence_studio.repositories.users;

import com.example.edusence_studio.models.users.TrainingProfessionalProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TrainingProfessionalProfileRepository extends JpaRepository<TrainingProfessionalProfile, UUID>, JpaSpecificationExecutor<TrainingProfessionalProfile> {
}
