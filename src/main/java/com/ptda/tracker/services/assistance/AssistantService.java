package com.ptda.tracker.services.assistance;

import com.ptda.tracker.models.assistance.Assistant;

import java.util.List;
import java.util.Optional;

public interface AssistantService {

    List<Assistant> getAll();

    Optional<Assistant> getById(Long id);

    Optional<Assistant> getByEmail(String email);

    Assistant create(Assistant assistant);

    List<Assistant> createAll(List<Assistant> assistants);

    Assistant update(Assistant assistant);

    boolean delete(Assistant assistant);

}
