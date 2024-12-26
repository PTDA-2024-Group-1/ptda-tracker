package com.ptda.tracker.repositories;

import com.ptda.tracker.models.admin.GlobalVariable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GlobalVariableRepository extends JpaRepository<GlobalVariable, String> {
    GlobalVariable findByKeyName(String keyName);
}
