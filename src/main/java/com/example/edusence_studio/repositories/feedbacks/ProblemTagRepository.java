package com.example.edusence_studio.repositories.feedbacks;

import com.example.edusence_studio.models.feedbacks.ProblemTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProblemTagRepository extends JpaRepository<ProblemTag, UUID> {
    Optional<ProblemTag> findByCode(String code);
    List<ProblemTag> findAllByOrderByCodeAsc();
}
