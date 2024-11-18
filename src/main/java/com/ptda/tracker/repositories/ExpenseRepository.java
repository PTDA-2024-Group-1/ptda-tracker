package com.ptda.tracker.repositories;

import com.ptda.tracker.models.tracker.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findAllByCreatedByIdAndBudgetNull(Long userId);

    List<Expense> findAllByBudgetId(Long budgetId);

}
