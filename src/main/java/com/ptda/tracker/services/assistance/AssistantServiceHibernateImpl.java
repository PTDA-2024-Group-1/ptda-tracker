package com.ptda.tracker.services.assistance;

import com.ptda.tracker.models.assistance.Assistant;
import com.ptda.tracker.repositories.AssistantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssistantServiceHibernateImpl implements AssistantService {

    private final AssistantRepository assistantRepository;

    @Override
    public Optional<Assistant> getById(Long id) {
        return assistantRepository.findById(id);
    }

    @Override
    public Optional<Assistant> getByEmail(String email) {
        return assistantRepository.findByEmail(email);
    }

    @Override
    public Assistant create(Assistant assistant) {
        return assistantRepository.save(assistant);
    }

    @Override
    public Assistant update(Assistant assistant) {
        return assistantRepository.save(assistant);
    }

    @Override
    public boolean delete(Assistant assistant) {
        if (assistantRepository.existsById(assistant.getId())) {
            assistantRepository.delete(assistant);
            return true;
        }
        return false;
    }

    @Override
    public List<Assistant> getAll() {
        return assistantRepository.findAll();
    }
}
