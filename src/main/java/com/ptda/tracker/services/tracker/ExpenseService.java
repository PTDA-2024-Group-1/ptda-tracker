package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.Expense;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ExpenseService {

    List<Expense> getAll();

    int getCount();

    List<Expense> getAllByBudgetId(Long budgetId);

    List<Expense> getAllByUserId(Long userId);

    List<Expense> getRecentExpensesByUserId(Long userId, int limit);

    List<Expense> getPersonalExpensesByUserId(Long userId);

    Map<String, Double> getExpensesByCategory(Long userId);

    List<Expense> getExpensesByBudgetIdWithPagination(Long budgetId, int offset, int limit);

    List<Expense> getPersonalExpensesByUserIdWithPagination(Long userId, int offset, int limit);

    double getTotalExpenseAmountByBudgetId(Long budgetId);

    int getCountByBudgetId(Long id);

    int getCountByUserId(Long userId);

    int getCountByUserIdPersonal(Long userId);

    Optional<Expense> getById(Long id);

    Expense create(Expense expense);

    List<Expense> createAll(List<Expense> expenses);

    Expense update(Expense expense);

    List<Expense> updateAll(List<Expense> expenses);

    boolean delete(Long id);

    boolean deleteAllPersonalExpensesByUserId(Long userId);

}