package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.repositories.ExpenseRepository;
import com.ptda.tracker.repositories.ExpenseDivisionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseServiceHibernateImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseDivisionRepository expenseDivisionRepository;

    @Override
    @Transactional
    public Expense create(Expense expense) {
        return expenseRepository.save(expense);
    }

    @Override
    public Optional<Expense> getById(Long id) {
        return expenseRepository.findById(id);
    }

    @Override
    public List<Expense> getAll() {
        return expenseRepository.findAll();
    }

    @Override
    public List<Expense> getAllByBudgetId(Long budgetId) {
        return expenseRepository.findAllByBudgetId(budgetId);
    }

    @Override
    public List<Expense> getAllByUserId(Long userId) {
        return expenseRepository.findAllByCreatedById(userId);
    }

    @Override
    public List<Expense> getPersonalExpensesByUserId(Long userId) {
        return expenseRepository.findAllByCreatedByIdAndBudgetNull(userId);
    }

    @Override
    public double getTotalExpenseAmountByBudgetId(Long budgetId) {
        return expenseRepository.findAllByBudgetId(budgetId).stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    @Override
    public Map<String, Double> getExpensesByCategory(Long userId) {
        List<Expense> expenses = expenseRepository.findAllByCreatedById(userId);
        return expenses.stream()
                .collect(Collectors.groupingBy(
                        expense -> expense.getCategory().toString(),
                        Collectors.summingDouble(Expense::getAmount)
                ));
    }

    @Override
    public List<Expense> getExpensesByBudgetIdWithPagination(Long budgetId, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        return expenseRepository.findByBudgetIdOrderByDateDesc(budgetId, pageable);
    }

    @Override
    public List<Expense> getPersonalExpensesByUserIdWithPagination(Long userId, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        return expenseRepository.findByCreatedByIdAndBudgetNullOrderByDateDesc(userId, pageable);
    }

    @Override
    @Transactional
    public Expense update(Expense expense) {
        return expenseRepository.save(expense);
    }

    @Override
    public Expense assignBudget(Long expenseId, Long budgetId) {
        Optional<Expense> optionalExpense = expenseRepository.findById(expenseId);
        if (optionalExpense.isPresent()) {
            Expense expense = optionalExpense.get();
            expense.setBudget(
                    Budget.builder()
                            .id(budgetId)
                            .build()
            );
            return expenseRepository.save(expense);
        }
        return null;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        if (expenseRepository.existsById(id)) {
            // Delete related subdivisions first
            expenseDivisionRepository.deleteByExpenseId(id);
            // Then delete the expense
            expenseRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteAllPersonalExpensesByUserId(Long userId) {
        List<Expense> personalExpenses = expenseRepository.findAllByCreatedByIdAndBudgetNull(userId);
        expenseRepository.deleteAll(personalExpenses);
        return true;
    }

    @Override
    public void saveAll(List<Expense> importedExpenses) {
        expenseRepository.saveAll(importedExpenses);
    }

    @Override
    public long countByBudgetId(Long id) {
        return expenseRepository.countByBudgetId(id);
    }
}