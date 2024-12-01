package com.ptda.tracker.repositories;

import com.ptda.tracker.models.assistance.Assistant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssistantRepository extends JpaRepository<Assistant, Long> {

    Optional<Assistant> findByEmail(String email);

}
