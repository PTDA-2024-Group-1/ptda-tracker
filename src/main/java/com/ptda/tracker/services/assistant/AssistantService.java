package com.ptda.tracker.services.assistant;

import com.ptda.tracker.models.assistance.Assistant;

import java.util.Optional;

public interface AssistantService {

    Optional<Assistant> getById(Long id);

    Optional<Assistant> getByEmail(String email);

    Assistant create(Assistant assistant);

    Assistant update(Assistant assistant);

    boolean delete(Assistant assistant);

}
