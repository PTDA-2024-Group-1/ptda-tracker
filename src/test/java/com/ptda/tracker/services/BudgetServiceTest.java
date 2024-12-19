package com.ptda.tracker.services;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.BudgetAccess;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.repositories.BudgetRepository;
import com.ptda.tracker.repositories.UserRepository;
import com.ptda.tracker.services.tracker.BudgetAccessService;
import com.ptda.tracker.services.tracker.BudgetServiceHibernateImpl;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.util.UserSession;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BudgetServiceTest {

    @Autowired
    private final BudgetRepository budgetRepository;

    @Autowired
    private final UserRepository userRepository;

    @Mock
    private BudgetAccessService budgetAccessService;

    @Mock
    private ExpenseService expenseService;

    @InjectMocks
    private BudgetServiceHibernateImpl budgetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTotalBudgetAmount() {
        User user = User.builder()
                .name("Test User")
                .email("user@example.com")
                .password("password")
                .build();
        userRepository.save(user);

        UserSession.getInstance().setUser(user);

        Budget budget = Budget.builder()
                .name("Test Budget")
                .createdBy(user)
                .build();
        budgetRepository.save(budget);


        when(expenseService.getTotalExpenseAmountByBudgetId(budget.getId())).thenReturn(0.0);


        double totalAmount = budgetService.getTotalBudgetAmount(budget.getId());


        assertThat(totalAmount).isEqualTo(0.0); // Assuming no expenses are added yet
    }

//    @Test
//    void testGetAllByUserId() {
//        // Given
//        User user = User.builder()
//                .name("Test User")
//                .email("user@example.com")
//                .password("password")
//                .build();
//        userRepository.save(user);
//
//        UserSession.getInstance().setUser(user);
//
//        Budget budget1 = Budget.builder()
//                .name("Test Budget 1")
//                .createdBy(user)
//                .build();
//        budgetRepository.save(budget1);
//
//        Budget budget2 = Budget.builder()
//                .name("Test Budget 2")
//                .createdBy(user)
//                .build();
//        budgetRepository.save(budget2);
//
//        // Mocking BudgetAccess to return the correct BudgetAccess objects
//        BudgetAccess budgetAccess1 = BudgetAccess.builder().budget(budget1).build();
//        BudgetAccess budgetAccess2 = BudgetAccess.builder().budget(budget2).build();
//
//        // Now mock BudgetAccessService to return a list of BudgetAccess
//        when(budgetAccessService.getAllByUserId(user.getId())).thenReturn(List.of(budgetAccess1, budgetAccess2));
//
//        // When
//        List<Budget> budgets = budgetService.getAllByUserId(user.getId());
//
//        // Then
//        assertThat(budgets).hasSize(2);
//        assertThat(budgets).extracting(Budget::getName).containsExactlyInAnyOrder("Test Budget 1", "Test Budget 2");
//    }
}