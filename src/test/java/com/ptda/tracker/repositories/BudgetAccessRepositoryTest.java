package com.ptda.tracker.repositories;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.BudgetAccess;
import com.ptda.tracker.models.tracker.BudgetAccessLevel;
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
public class BudgetAccessRepositoryTest {

    private final BudgetAccessRepository budgetAccessRepository;
    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;

    @Test
    void testSaveAndFindByUserId() {
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

        BudgetAccess budgetAccess = BudgetAccess.builder()
                .accessLevel(BudgetAccessLevel.OWNER)
                .budget(budget)
                .user(user)
                .build();
        budgetAccessRepository.save(budgetAccess);
        assertThat(budgetAccess.getId()).isNotNull();

        List<BudgetAccess> accesses = budgetAccessRepository.findAllByUserId(user.getId());
        assertThat(accesses).isNotEmpty();
        assertThat(accesses.get(0).getAccessLevel()).isEqualTo(BudgetAccessLevel.OWNER);
    }

    @Test
    void testFindByBudgetIdAndUserId() {
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

        BudgetAccess budgetAccess = BudgetAccess.builder()
                .accessLevel(BudgetAccessLevel.OWNER)
                .budget(budget)
                .user(user)
                .build();
        budgetAccessRepository.save(budgetAccess);

        Optional<BudgetAccess> retrieved = budgetAccessRepository.findByBudgetIdAndUserId(budget.getId(), user.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getAccessLevel()).isEqualTo(BudgetAccessLevel.OWNER);
    }

    @Test
    void testDeleteAllByUserId() {
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

        BudgetAccess budgetAccess = BudgetAccess.builder()
                .accessLevel(BudgetAccessLevel.OWNER)
                .budget(budget)
                .user(user)
                .build();
        budgetAccessRepository.save(budgetAccess);

        budgetAccessRepository.deleteAllByUserId(user.getId());

        List<BudgetAccess> accesses = budgetAccessRepository.findAllByUserId(user.getId());
        assertThat(accesses).isEmpty();
    }

    @Test
    void testFindAllByBudgetId() {
        User user1 = User.builder()
                .name("User 1")
                .email("user1@example.com")
                .password("password")
                .build();
        userRepository.save(user1);

        UserSession.getInstance().setUser(user1);

        User user2 = User.builder()
                .name("User 2")
                .email("user2@example.com")
                .password("password")
                .build();
        userRepository.save(user2);

        UserSession.getInstance().setUser(user2);

        Budget budget = Budget.builder()
                .name("Test Budget")
                .description("Test Description")
                .build();
        budgetRepository.save(budget);

        BudgetAccess access1 = BudgetAccess.builder()
                .accessLevel(BudgetAccessLevel.OWNER)
                .budget(budget)
                .user(user1)
                .build();
        budgetAccessRepository.save(access1);

        BudgetAccess access2 = BudgetAccess.builder()
                .accessLevel(BudgetAccessLevel.EDITOR)
                .budget(budget)
                .user(user2)
                .build();
        budgetAccessRepository.save(access2);

        List<BudgetAccess> accesses = budgetAccessRepository.findAllByBudgetId(budget.getId());
        assertThat(accesses).hasSize(2);
        assertThat(accesses).extracting(BudgetAccess::getAccessLevel)
                .containsExactlyInAnyOrder(BudgetAccessLevel.OWNER, BudgetAccessLevel.EDITOR);
    }

    @Test
    void testExistsByBudgetIdAndUserId() {
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

        BudgetAccess budgetAccess = BudgetAccess.builder()
                .accessLevel(BudgetAccessLevel.OWNER)
                .budget(budget)
                .user(user)
                .build();
        budgetAccessRepository.save(budgetAccess);

        boolean exists = budgetAccessRepository.existsByBudgetIdAndUserId(budget.getId(), user.getId());
        assertThat(exists).isTrue();

        boolean notExists = budgetAccessRepository.existsByBudgetIdAndUserId(999L, user.getId());
        assertThat(notExists).isFalse();
    }

}