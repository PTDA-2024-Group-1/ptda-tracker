package com.ptda.tracker.repositories;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.BudgetAccess;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.tracker.BudgetAccessService;
import com.ptda.tracker.services.tracker.ExpenseService;
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
public class BudgetRepositoryTest {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    @Test
    void testSaveAndFindByCreatedById() {
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
                .build();
        budgetRepository.save(budget);
        assertThat(budget.getId()).isNotNull();

        List<Budget> budgets = budgetRepository.findAllByCreatedById(user.getId());
        assertThat(budgets).isNotEmpty();
        assertThat(budgets.get(0).getName()).isEqualTo("Test Budget");
    }

    @Test
    void testUpdateBudget() {
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
                .build();
        budgetRepository.save(budget);

        budget.setName("Updated Budget");
        budgetRepository.save(budget);

        Optional<Budget> retrieved = budgetRepository.findById(budget.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName()).isEqualTo("Updated Budget");
    }

    @Test
    void testDeleteBudget() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();
        userRepository.save(user);

        Budget budget = Budget.builder()
                .name("Test Budget")
                .description("Test Description")
                .build();
        budgetRepository.save(budget);
        budgetRepository.delete(budget);

        Optional<Budget> retrieved = budgetRepository.findById(budget.getId());
        assertThat(retrieved).isNotPresent();
    }

    @Test
    void testPrePersist() {
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
                .build();
        budgetRepository.save(budget);

        assertThat(budget.getCreatedAt()).isNotNull();
        assertThat(budget.getCreatedBy()).isEqualTo(user);
    }

    @Test
    void testFindAllByCreatedByIdWithMultipleBudgets() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();
        userRepository.save(user);

        UserSession.getInstance().setUser(user);

        Budget budget1 = Budget.builder()
                .name("Test Budget 1")
                .description("Test Description 1")
                .build();
        budgetRepository.save(budget1);

        Budget budget2 = Budget.builder()
                .name("Test Budget 2")
                .description("Test Description 2")
                .build();
        budgetRepository.save(budget2);

        List<Budget> budgets = budgetRepository.findAllByCreatedById(user.getId());
        assertThat(budgets).hasSize(2);
        assertThat(budgets).extracting(Budget::getName).containsExactly("Test Budget 1", "Test Budget 2");
    }

    @Test
    void testSetAndRetrieveFavoriteBudget() {
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
                .isFavorite(true)
                .build();
        budgetRepository.save(budget);

        Optional<Budget> retrieved = budgetRepository.findById(budget.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().isFavorite()).isTrue();
    }

    @Test
    void testFindAll(){
        User user = User.builder()
                .name("Test User")
                .email("dsad@da.com")
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
        Budget budget2 = Budget.builder()
                .name("Test Budget2")
                .description("Test Description2")
                .createdBy(user)
                .build();
        budgetRepository.save(budget2);
        List<Budget> budgets = budgetRepository.findAll();
        assertThat(budgets).isNotEmpty();
    }

    @Test
    void testCount() {
        User user = User.builder()
                .name("Test User")
                .email("email@user.com")
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

        int count = (int) budgetRepository.count();
        assertThat(count).isEqualTo(1);
    }

    @Test
    void testSaveAll() {
        User user = User.builder()
                .name("Test User")
                .email("test@email.com")
                .password("password")
                .build();
        userRepository.save(user);

        UserSession.getInstance().setUser(user);

        Budget budget1 = Budget.builder()
                .name("Test Budget 1")
                .description("Test Description 1")
                .createdBy(user)
                .build();
        Budget budget2 = Budget.builder()
                .name("Test Budget 2")
                .description("Test Description 2")
                .createdBy(user)
                .build();
        List<Budget> budgets = List.of(budget1, budget2);
        budgetRepository.saveAll(budgets);

        List<Budget> retrieved = budgetRepository.findAll();
        assertThat(retrieved).hasSize(2);

        assertThat(retrieved).extracting(Budget::getName).containsExactly("Test Budget 1", "Test Budget 2");
    }

    @Test
    void testDeleteById(){
        User user = User.builder()
                .name("Test User")
                .email("email@user.com")
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

        budgetRepository.deleteById(budget.getId());
        Optional<Budget> retrieved = budgetRepository.findById(budget.getId());
        assertThat(retrieved).isNotPresent();

    }


}