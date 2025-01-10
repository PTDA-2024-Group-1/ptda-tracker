package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.BudgetAccess;
import com.ptda.tracker.models.tracker.BudgetAccessLevel;
import com.ptda.tracker.repositories.BudgetRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetServiceHibernateImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final BudgetAccessService budgetAccessService;
    private final ExpenseService expenseService;

    @Override
    public Optional<Budget> getById(Long id) {
        return budgetRepository.findById(id);
    }

    @Override
    public List<Budget> getAllByUserId(Long userId) {
        return budgetAccessService.getAllByUserId(userId).stream()
                .map(BudgetAccess::getBudget)
                .collect(Collectors.toList());
    }

    @Override
    public List<Budget> getRecentByUserId(Long userId, int limit) {
        return budgetAccessService.getRecentByUserId(userId, limit).stream()
                .map(BudgetAccess::getBudget)
                .collect(Collectors.toList());
    }

    @Override
    public List<Budget> getAll() {
        return budgetRepository.findAll();
    }

    @Override
    public int getCount() {
        return (int) budgetRepository.count();
    }

    @Override
    public int getCountByUserId(Long userId) {
        return budgetAccessService.getAllByUserId(userId).size();
    }


    @Override
    public double getTotalBudgetAmount(Long budgetId) {
        return expenseService.getTotalExpenseAmountByBudgetId(budgetId);
    }

    @Override
    @Transactional
    public Budget create(Budget budget) {
        budget = budgetRepository.save(budget);
        budgetAccessService.create(budget.getId(), budget.getCreatedBy().getId(), BudgetAccessLevel.OWNER);
        return budget;
    }

    @Override
    public List<Budget> createAll(List<Budget> budgets) {
        return budgetRepository.saveAll(budgets);
    }

    @Override
    @Transactional
    public Budget update(Budget budget) {
        return budgetRepository.save(budget);
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        if (budgetRepository.existsById(id)) {
            budgetRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Budget[] findAll() {
        return budgetRepository.findAll().toArray(new Budget[0]);
    }

}
