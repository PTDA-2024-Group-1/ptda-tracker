package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.repositories.ExpenseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExpenseServiceHibernateImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;

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
            expenseRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
