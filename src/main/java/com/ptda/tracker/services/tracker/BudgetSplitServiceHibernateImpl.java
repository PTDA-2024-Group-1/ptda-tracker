package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.BudgetAccess;
import com.ptda.tracker.models.tracker.BudgetSplit;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.tracker.ExpenseDivision;
import com.ptda.tracker.repositories.BudgetSplitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BudgetSplitServiceHibernateImpl implements BudgetSplitService {

    private final BudgetAccessService budgetAccessService;
    private final BudgetSplitRepository budgetSplitRepository;
    private final ExpenseService expenseService;
    private final ExpenseDivisionService expenseDivisionService;

    @Override
    public List<BudgetSplit> getAllByBudgetId(Long budgetId) {
        if (!budgetSplitRepository.getAllByBudgetId(budgetId).isEmpty()) {
            return budgetSplitRepository.getAllByBudgetId(budgetId);
        } else {
            return split(budgetId);
        }
    }

    /**
     * Splits the budget among users based on their expenses and custom divisions.
     *
     * @param budgetId the ID of the budget to split
     * @return a list of BudgetSplit objects representing the split amounts for each user
     */
    @Override
    public List<BudgetSplit> split(Long budgetId) {
        List<BudgetSplit> splits = new ArrayList<>();

        // Retrieve accesses and expenses for the budget
        List<BudgetAccess> accesses = budgetAccessService.getAllByBudgetId(budgetId);
        List<Expense> expenses = expenseService.getAllByBudgetId(budgetId);

        if (accesses.isEmpty() || expenses.isEmpty()) {
            return splits; // Return empty list if no users or expenses are found
        }

        // Map to track user splits
        Map<Long, BudgetSplit> userSplits = new HashMap<>();

        for (Expense expense : expenses) {
            BigDecimal totalExpense = BigDecimal.valueOf(expense.getAmount());
            BigDecimal remainingExpense = totalExpense;

            List<Long> usersWithDivision = new ArrayList<>();

            // Handle custom divisions
            List<ExpenseDivision> divisions = expenseDivisionService.getAllByExpenseId(expense.getId());
            if (divisions != null) {
                for (ExpenseDivision division : divisions) {
                    BigDecimal userShare = BigDecimal.valueOf(division.getAmount()).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal paidAmount = BigDecimal.valueOf(division.getPaidAmount()).setScale(2, RoundingMode.HALF_UP);

                    // Update or create a BudgetSplit for the user
                    userSplits.compute(division.getUser().getId(), (userId, split) -> {
                        if (split == null) {
                            split = BudgetSplit.builder()
                                    .paidAmount(paidAmount.doubleValue())
                                    .amount(userShare.doubleValue())
                                    .user(division.getUser())
                                    .budget(expense.getBudget())
                                    .build();
                        } else {
                            split.setPaidAmount(BigDecimal.valueOf(split.getPaidAmount()).add(paidAmount).doubleValue());
                            split.setAmount(BigDecimal.valueOf(split.getAmount()).add(userShare).doubleValue());
                        }
                        return split;
                    });

                    usersWithDivision.add(division.getUser().getId());
                    remainingExpense = remainingExpense.subtract(userShare);
                }
            }

            // Divide remaining expense among users without custom divisions
            List<BudgetAccess> usersWithoutDivision = accesses.stream()
                    .filter(access -> !usersWithDivision.contains(access.getUser().getId()))
                    .toList();

            if (!usersWithoutDivision.isEmpty()) {
                BigDecimal perUserAmount = remainingExpense.divide(
                        BigDecimal.valueOf(usersWithoutDivision.size()), 2, RoundingMode.HALF_UP);

                for (BudgetAccess access : usersWithoutDivision) {
                    userSplits.compute(access.getUser().getId(), (userId, split) -> {
                        if (split == null) {
                            split = BudgetSplit.builder()
                                    .amount(perUserAmount.doubleValue())
                                    .paidAmount(access.getUser().getId().equals(expense.getCreatedBy().getId())
                                            ? totalExpense.doubleValue()
                                            : 0)
                                    .user(access.getUser())
                                    .budget(expense.getBudget())
                                    .build();
                        } else {
                            split.setAmount(BigDecimal.valueOf(split.getAmount()).add(perUserAmount).doubleValue());
                            if (access.getUser().getId().equals(expense.getCreatedBy().getId())) {
                                split.setPaidAmount(BigDecimal.valueOf(split.getPaidAmount())
                                        .add(totalExpense).doubleValue());
                            }
                        }
                        return split;
                    });
                }
            }
        }

        // Adjust rounding discrepancies
        BigDecimal totalPaid = userSplits.values().stream()
                .map(split -> BigDecimal.valueOf(split.getPaidAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAmount = userSplits.values().stream()
                .map(split -> BigDecimal.valueOf(split.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discrepancy = totalPaid.subtract(totalAmount).setScale(2, RoundingMode.HALF_UP);

        if (discrepancy.compareTo(BigDecimal.ZERO) != 0) {
            userSplits.values().iterator().next().setAmount(
                    BigDecimal.valueOf(userSplits.values().iterator().next().getAmount())
                            .add(discrepancy).doubleValue());
        }

        splits.addAll(userSplits.values());
        return budgetSplitRepository.saveAll(splits);
    }
}
