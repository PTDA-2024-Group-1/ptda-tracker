package com.ptda.tracker.services.administration;

import com.ptda.tracker.models.admin.GlobalVariable;
import com.ptda.tracker.models.admin.GlobalVariableName;
import com.ptda.tracker.repositories.GlobalVariableRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GlobalVariableServiceHibernateImpl implements GlobalVariableService {
    private final GlobalVariableRepository globalVariableRepository;

    @Override
    public String get(String keyName) {
        GlobalVariable globalVariable = globalVariableRepository.findByKeyName(keyName);
        return globalVariable != null ? globalVariable.getValue() : null;
    }

    @Override
    @Transactional
    public void set(GlobalVariableName name, String value) {
        GlobalVariable globalVariable = GlobalVariable.builder()
                .keyName(name.name())
                .value(value)
                .build();
        try {
            globalVariableRepository.save(globalVariable);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set global variable: " + name.name(), e);
        }
    }

    @Override
    @Transactional
    public void delete(String keyName) {
        globalVariableRepository.deleteById(keyName);
    }
}
