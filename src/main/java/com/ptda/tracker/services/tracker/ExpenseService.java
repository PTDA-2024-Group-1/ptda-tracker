package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.Expense;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ExpenseService {

    Optional<Expense> getById(Long id);

    List<Expense> getAll();

    List<Expense> getAllByBudgetId(Long budgetId);

    List<Expense> getAllByUserId(Long userId);

    List<Expense> getPersonalExpensesByUserId(Long userId);

    double getTotalExpenseAmountByBudgetId(Long budgetId);

    Map<String, Double> getExpensesByCategory(Long userId);

    List<Expense> getExpensesByBudgetIdWithPagination(Long budgetId, int offset, int limit);

    List<Expense> getPersonalExpensesByUserIdWithPagination(Long userId, int offset, int limit);

    long getCountByBudgetId(Long id);

    Expense create(Expense expense);

    List<Expense> createAll(List<Expense> expenses);

    Expense update(Expense expense);

    List<Expense> updateAll(List<Expense> expenses);

    Expense assignBudget(Long expenseId, Long budgetId);

    boolean delete(Long id);

    boolean deleteAllPersonalExpensesByUserId(Long userId);

}