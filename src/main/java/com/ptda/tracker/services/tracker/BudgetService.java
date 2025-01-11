package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.Budget;

import java.util.List;
import java.util.Optional;

public interface BudgetService {

    Optional<Budget> getById(Long id);

    List<Budget> getAllByUserId(Long userId);

    List<Budget> getRecentByUserId(Long userId, int limit);

    List<Budget> getAll();

    int getCount();

    int getCountByUserId(Long userId);

    double getTotalBudgetAmount(Long userId);

    Budget create(Budget budget);

    List<Budget> createAll(List<Budget> budgets);

    Budget update(Budget budget);

    Budget update();

    boolean delete(Long id);

    Budget[] findAll();
}
