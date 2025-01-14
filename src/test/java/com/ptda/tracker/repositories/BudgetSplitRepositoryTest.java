package com.ptda.tracker.repositories;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.BudgetSplit;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.util.UserSession;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BudgetSplitRepositoryTest {

    private final BudgetSplitRepository budgetSplitRepository;
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    @Test
    void testSaveAndFindByBudgetId() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();
        userRepository.save(user);
        assertThat(user.getId()).isNotNull();

        UserSession.getInstance().setUser(user);

        Budget budget = Budget.builder()
                .name("Test Budget")
                .description("Test Description")
                .createdBy(user)
                .build();
        budgetRepository.save(budget);
        assertThat(budget.getId()).isNotNull();

        BudgetSplit budgetSplit = BudgetSplit.builder()
                .amount(100.0)
                .paidAmount(50.0)
                .user(user)
                .budget(budget)
                .build();
        budgetSplitRepository.save(budgetSplit);
        assertThat(budgetSplit.getId()).isNotNull();

        List<BudgetSplit> splits = budgetSplitRepository.getAllByBudgetId(budget.getId());
        assertThat(splits).isNotEmpty();
        assertThat(splits.get(0).getAmount()).isEqualTo(100.0);
    }

    @Test
    void testUpdateBudgetSplit() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();
        userRepository.save(user);

        UserSession.getInstance().setUser(user);

        Budget budget = Budget.builder()
                .name("Test Budget")
                .description("Test Description")
                .createdBy(user)
                .build();
        budgetRepository.save(budget);

        BudgetSplit budgetSplit = BudgetSplit.builder()
                .amount(100.0)
                .paidAmount(50.0)
                .user(user)
                .budget(budget)
                .build();
        budgetSplitRepository.save(budgetSplit);

        budgetSplit.setAmount(200.0);
        budgetSplitRepository.save(budgetSplit);

        Optional<BudgetSplit> retrieved = budgetSplitRepository.findById(budgetSplit.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getAmount()).isEqualTo(200.0);
    }

    @Test
    void testDeleteBudgetSplit() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();
        userRepository.save(user);

        Budget budget = Budget.builder()
                .name("Test Budget")
                .description("Test Description")
                .createdBy(user)
                .build();
        budgetRepository.save(budget);

        BudgetSplit budgetSplit = BudgetSplit.builder()
                .amount(100.0)
                .paidAmount(50.0)
                .user(user)
                .budget(budget)
                .build();
        budgetSplitRepository.save(budgetSplit);
        budgetSplitRepository.delete(budgetSplit);

        Optional<BudgetSplit> retrieved = budgetSplitRepository.findById(budgetSplit.getId());
        assertThat(retrieved).isNotPresent();
    }

    @Test
    void testFindAllByBudgetIdWithMultipleSplits() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();
        userRepository.save(user);

        UserSession.getInstance().setUser(user);

        Budget budget = Budget.builder()
                .name("Test Budget")
                .description("Test Description")
                .createdBy(user)
                .build();
        budgetRepository.save(budget);

        BudgetSplit split1 = BudgetSplit.builder()
                .amount(100.0)
                .paidAmount(50.0)
                .user(user)
                .budget(budget)
                .build();
        budgetSplitRepository.save(split1);

        BudgetSplit split2 = BudgetSplit.builder()
                .amount(200.0)
                .paidAmount(100.0)
                .user(user)
                .budget(budget)
                .build();
        budgetSplitRepository.save(split2);

        List<BudgetSplit> splits = budgetSplitRepository.getAllByBudgetId(budget.getId());
        assertThat(splits).hasSize(2);
        assertThat(splits).extracting(BudgetSplit::getAmount).containsExactly(100.0, 200.0);
    }
}