package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.Budget;

import java.util.List;
import java.util.Optional;

public interface BudgetService {

    Optional<Budget> getById(Long id);

    List<Budget> getAllByUserId(Long userId);

    List<Budget> getAll();

    double getTotalBudgetAmount(Long userId);

    Budget create(Budget budget);

    Budget update(Budget budget);

    Budget updateWithoutAudit(Budget budget); // Newly added method

    boolean delete(Long id);

    Budget[] findAll();
}
