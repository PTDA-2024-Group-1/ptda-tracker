package com.ptda.tracker.repositories;

import com.ptda.tracker.models.tracker.Expense;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findAllByCreatedByIdAndBudgetNull(Long userId);

    List<Expense> findAllByBudgetId(Long budgetId);

    List<Expense> findAllByCreatedById(Long userId);

    List<Expense> findByBudgetIdOrderByDateDesc(Long budgetId, Pageable pageable);

    List<Expense> findByCreatedByIdAndBudgetNullOrderByDateDesc(Long userId, Pageable pageable);

    int countByBudgetId(Long id);

    int countByCreatedById(Long userId);

    int countByCreatedByIdAndBudgetNull(Long userId);

    List<Expense> findTopByCreatedByIdOrderByDateDesc(Long userId, Pageable pageable);

}
