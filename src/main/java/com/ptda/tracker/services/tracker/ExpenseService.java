package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.Expense;

import java.util.List;
import java.util.Optional;

public interface ExpenseService {

    Optional<Expense> getById(Long id);

    List<Expense> getAll();

    List<Expense> getAllByBudgetId(Long budgetId);

    List<Expense> getAllByUserId(Long userId);

    List<Expense> getPersonalExpensesByUserId(Long userId);

    double getTotalExpenseAmountByBudgetId(Long budgetId);

    Expense create(Expense expense);

    Expense update(Expense expense);

    Expense assignBudget(Long expenseId, Long budgetId);

    boolean delete(Long id);

}
