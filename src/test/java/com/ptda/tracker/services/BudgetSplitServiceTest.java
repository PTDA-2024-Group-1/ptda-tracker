package com.ptda.tracker.services;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.BudgetAccess;
import com.ptda.tracker.models.tracker.BudgetSplit;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.tracker.ExpenseDivision;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.repositories.BudgetRepository;
import com.ptda.tracker.repositories.BudgetSplitRepository;
import com.ptda.tracker.repositories.ExpenseRepository;
import com.ptda.tracker.repositories.UserRepository;
import com.ptda.tracker.services.tracker.BudgetAccessService;
import com.ptda.tracker.services.tracker.BudgetSplitService;
import com.ptda.tracker.services.tracker.ExpenseDivisionService;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.util.UserSession;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BudgetSplitServiceTest {

    private final BudgetSplitService budgetSplitService;
    private final BudgetSplitRepository budgetSplitRepository;
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetAccessService budgetAccessService;
    private final ExpenseService expenseService;
    private final ExpenseDivisionService expenseDivisionService;

    @BeforeEach
    void setUp() {
        String testEmail = "test@example.com";
        userRepository.findByEmail(testEmail).ifPresent(userRepository::delete);

        User user = User.builder()
                .name("Test User")
                .email(testEmail)
                .password("password")
                .build();
        userRepository.save(user);
        UserSession.getInstance().setUser(user);
    }

    @Test
    void testSplit() {
        User user = userRepository.findByEmail("test@example.com").orElseThrow();
        Budget budget = Budget.builder()
                .name("Test Budget")
                .description("Test Description")
                .createdBy(user)
                .build();
        budgetRepository.save(budget);

        BudgetAccess budgetAccess = BudgetAccess.builder()
                .budget(budget)
                .user(user)
                .build();
        budgetAccessService.update(budgetAccess);

        Expense expense = Expense.builder()
                .title("Test Expense")
                .amount(100.0)
                .budget(budget)
                .createdBy(user)
                .build();
        expenseRepository.save(expense);

        ExpenseDivision expenseDivision = ExpenseDivision.builder()
                .expense(expense)
                .user(user)
                .amount(100.0)
                .paidAmount(50.0)
                .build();
        expenseDivisionService.update(expenseDivision);

        List<BudgetSplit> splits = budgetSplitService.split(budget.getId());
        assertThat(splits).isNotEmpty();
        assertThat(splits.get(0).getAmount()).isEqualTo(100.0);
        assertThat(splits.get(0).getPaidAmount()).isEqualTo(50.0);
    }
}