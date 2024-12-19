package com.ptda.tracker.services;

import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.tracker.ExpenseCategory;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.repositories.ExpenseRepository;
import com.ptda.tracker.repositories.UserRepository;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.util.UserSession;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ExpenseServiceTest {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final ExpenseService expenseService;

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
    void testGetById() {
        Expense expense = new Expense();
        expense.setTitle("Test Expense");
        expense.setAmount(100.0);
        expense.setDate(new Date());
        expense.setDescription("Test Description");
        expense.setCategory(ExpenseCategory.OTHER);
        expenseRepository.save(expense);

        Optional<Expense> result = expenseService.getById(expense.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Test Expense");
    }

}