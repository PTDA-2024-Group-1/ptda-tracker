package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.BudgetAccess;
import com.ptda.tracker.models.tracker.BudgetSplit;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.tracker.ExpenseDivision;
import com.ptda.tracker.repositories.BudgetSplitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            double totalExpense = expense.getAmount();
            double remainingExpense = totalExpense;

            // Track users with custom divisions
            List<Long> usersWithDivision = new ArrayList<>();

            // Handle custom divisions
            List<ExpenseDivision> divisions = expenseDivisionService.getAllByExpenseId(expense.getId());
            if (divisions != null) {
                for (ExpenseDivision division : divisions) {
                    double userShare = division.getAmount();
                    double paidAmount = division.getPaidAmount();

                    // Update or create a BudgetSplit for the user
                    userSplits.compute(division.getUser().getId(), (userId, split) -> {
                        if (split == null) {
                            split = BudgetSplit.builder()
                                    .paidAmount(paidAmount)
                                    .amount(userShare)
                                    .user(division.getUser())
                                    .budget(expense.getBudget())
                                    .build();
                        } else {
                            split.setPaidAmount(split.getPaidAmount() + paidAmount);
                            split.setAmount(split.getAmount() + userShare);
                        }
                        return split;
                    });

                    usersWithDivision.add(division.getUser().getId());
                    remainingExpense -= userShare;
                }
            }

            // Divide remaining expense among users without custom divisions
            List<BudgetAccess> usersWithoutDivision = accesses.stream()
                    .filter(access -> !usersWithDivision.contains(access.getUser().getId()))
                    .toList();

            if (!usersWithoutDivision.isEmpty()) {
                double perUserAmount = remainingExpense / usersWithoutDivision.size();

                for (BudgetAccess access : usersWithoutDivision) {
                    userSplits.compute(access.getUser().getId(), (userId, split) -> {
                        if (split == null) {
                            split = BudgetSplit.builder()
                                    .amount(perUserAmount)
                                    // if creator of the expense then set contribution to the total expense
                                    .paidAmount(access.getUser().getId().equals(expense.getCreatedBy().getId()) ? totalExpense : 0)
                                    .user(access.getUser())
                                    .budget(expense.getBudget())
                                    .build();
                        } else {
                            split.setAmount(split.getAmount() + perUserAmount);
                            if (access.getUser().getId().equals(expense.getCreatedBy().getId())) {
                                split.setPaidAmount(split.getPaidAmount() + totalExpense);
                            }
                        }
                        return split;
                    });
                }
            }
        }

        splits.addAll(userSplits.values());
        return budgetSplitRepository.saveAll(splits);
    }
}
