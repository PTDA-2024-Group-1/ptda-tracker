package com.ptda.tracker.repositories;

import com.ptda.tracker.models.tracker.ExpenseColumnMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseColumnMappingRepository extends JpaRepository<ExpenseColumnMapping, Long> {
    List<ExpenseColumnMapping> findAllByCreatedBy_Id(Long userId);
}
