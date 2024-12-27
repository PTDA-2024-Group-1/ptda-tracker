package com.ptda.tracker.repositories;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.tracker.ExpenseCategory;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.util.UserSession;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ExpenseRepositoryTest {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    @Autowired
    private BudgetRepository budgetRepository;

    @Test
    void testInsertExpense() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();
        userRepository.save(user);

        UserSession.getInstance().setUser(user);

        Expense expense = new Expense();
        expense.setTitle("Test Expense");
        expense.setAmount(100.0);
        expense.setDate(new Date());
        expense.setDescription("Test Description");
        expense.setCategory(ExpenseCategory.OTHER);

        Expense savedExpense = expenseRepository.save(expense);
        assertThat(savedExpense.getId()).isNotNull();
    }

    @Test
    void testFindByCreatedById() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();
        userRepository.save(user);
        assertThat(user.getId()).isNotNull();

        UserSession.getInstance().setUser(user);

        Expense expense = new Expense();
        expense.setTitle("Test Expense");
        expense.setAmount(100.0);
        expense.setDate(new Date());
        expense.setDescription("Test Description");
        expense.setCategory(ExpenseCategory.OTHER);
        expenseRepository.save(expense);
        assertThat(expense.getId()).isNotNull();

        List<Expense> expenses = expenseRepository.findAllByCreatedById(user.getId());
        assertThat(expenses).isNotEmpty();
        assertThat(expenses.get(0).getTitle()).isEqualTo("Test Expense");
    }

    @Test
    void testUpdateExpense() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();
        userRepository.save(user);

        UserSession.getInstance().setUser(user);

        Expense expense = new Expense();
        expense.setTitle("Test Expense");
        expense.setAmount(100.0);
        expense.setDate(new Date());
        expense.setDescription("Test Description");
        expense.setCategory(ExpenseCategory.OTHER);

        expenseRepository.save(expense);

        expense.setTitle("Updated Expense");
        expenseRepository.save(expense);

        Optional<Expense> retrieved = expenseRepository.findById(expense.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getTitle()).isEqualTo("Updated Expense");
    }

    @Test
    void testDeleteExpense() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();
        userRepository.save(user);

        Expense expense = new Expense();
        expense.setTitle("Test Expense");
        expense.setAmount(100.0);
        expense.setDate(new Date());
        expense.setDescription("Test Description");
        expense.setCategory(ExpenseCategory.OTHER);
        expense.setCreatedBy(user);
        expense.setCreatedAt(System.currentTimeMillis());

        expenseRepository.save(expense);
        expenseRepository.delete(expense);

        Optional<Expense> retrieved = expenseRepository.findById(expense.getId());
        assertThat(retrieved).isNotPresent();
    }

    @Test
    void testExpenseCategoryDefault() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();
        userRepository.save(user);

        Expense expense = new Expense();
        expense.setTitle("Test Expense");
        expense.setAmount(100.0);
        expense.setDate(new Date());
        expense.setDescription("Test Description");
        expense.setCreatedBy(user);

        Expense savedExpense = expenseRepository.save(expense);
        assertThat(savedExpense.getCategory()).isEqualTo(ExpenseCategory.OTHER);
    }

    @Test
    void testFindAllByCreatedByIdWithMultipleExpenses() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();
        userRepository.save(user);

        UserSession.getInstance().setUser(user);

        Expense expense1 = new Expense();
        expense1.setTitle("Test Expense 1");
        expense1.setAmount(100.0);
        expense1.setDate(new Date());
        expense1.setDescription("Test Description 1");
        expense1.setCategory(ExpenseCategory.OTHER);
        expenseRepository.save(expense1);

        Expense expense2 = new Expense();
        expense2.setTitle("Test Expense 2");
        expense2.setAmount(200.0);
        expense2.setDate(new Date());
        expense2.setDescription("Test Description 2");
        expense2.setCategory(ExpenseCategory.OTHER);
        expenseRepository.save(expense2);

        List<Expense> expenses = expenseRepository.findAllByCreatedById(user.getId());
        assertThat(expenses).hasSize(2);
        assertThat(expenses).extracting(Expense::getTitle).containsExactly("Test Expense 1", "Test Expense 2");
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

        Expense expense = new Expense();
        expense.setTitle("Test Expense");
        expense.setAmount(100.0);
        expense.setDate(new Date());
        expense.setDescription("Test Description");
        expense.setCategory(ExpenseCategory.OTHER);
        expenseRepository.save(expense);

        assertThat(expense.getCreatedAt()).isNotNull();
        assertThat(expense.getCreatedBy()).isEqualTo(user);
    }

    @Test
    void testUpdateExpenseCategory() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();
        userRepository.save(user);

        UserSession.getInstance().setUser(user);

        Expense expense = new Expense();
        expense.setTitle("Test Expense");
        expense.setAmount(100.0);
        expense.setDate(new Date());
        expense.setDescription("Test Description");
        expense.setCategory(ExpenseCategory.OTHER);
        expenseRepository.save(expense);

        expense.setCategory(ExpenseCategory.FOOD); // update category
        expenseRepository.save(expense);

        Optional<Expense> updatedExpense = expenseRepository.findById(expense.getId());
        assertThat(updatedExpense).isPresent();
        assertThat(updatedExpense.get().getCategory()).isEqualTo(ExpenseCategory.FOOD);
    }

    @Test
    void testGetAllByBudgetId() {

        User user = User.builder()
                .name("Test User")
                .email("test@user.com")
                .password("password")
                .build();
        userRepository.save(user);
        UserSession.getInstance().setUser(user);

        Budget budget = new Budget();
        budget.setName("Test Budget");
        budget.setDescription("Test Description");
        budgetRepository.save(budget);

        Expense expense = new Expense();
        expense.setTitle("Test Expense");
        expense.setAmount(100.0);
        expense.setDate(new Date());
        expense.setCategory(ExpenseCategory.OTHER);
        expense.setCreatedBy(user);
        expense.setBudget(budget);
        expenseRepository.save(expense);

        List<Expense> expenses = expenseRepository.findAllByBudgetId(expense.getBudget().getId());
        assertThat(expenses).isNotEmpty();
    }

    @Test
    void testCountExpensesByBudget() {

        User user = User.builder()
                .name("Test User")
                .email("test@email.com")
                .password("password")
                .build();

        userRepository.save(user);
        UserSession.getInstance().setUser(user);

        Budget budget = new Budget();
        budget.setName("Test Budget");
        budget.setDescription("Test Description");
        budgetRepository.save(budget);

        Expense expense = new Expense();
        expense.setTitle("Test Expense");
        expense.setAmount(100.0);
        expense.setDate(new Date());
        expense.setCategory(ExpenseCategory.OTHER);
        expense.setCreatedBy(user);
        expense.setBudget(budget);
        expenseRepository.save(expense);

        long count = expenseRepository.countByBudgetId(budget.getId());
        assertThat(count).isEqualTo(1);

    }

    @Test
    void testFindAllExpenses(){

        User user = User.builder()
                .name("Test User")
                .email("test@email.com")
                .password("password")
                .build();

        userRepository.save(user);
        UserSession.getInstance().setUser(user);

        Expense expense = new Expense();
        expense.setTitle("Test Expense");
        expense.setAmount(100.0);
        expense.setDate(new Date());
        expense.setCategory(ExpenseCategory.OTHER);
        expense.setCreatedBy(user);
        expenseRepository.save(expense);

        Expense expense2 = new Expense();
        expense2.setTitle("Test Expense 2");
        expense2.setAmount(200.0);
        expense2.setDate(new Date());
        expense2.setCategory(ExpenseCategory.OTHER);
        expense2.setCreatedBy(user);
        expenseRepository.save(expense2);

        List<Expense> expenses = expenseRepository.findAll();
        assertThat(expenses).isNotEmpty();
    }

    @Test
    void TestFindAllByCreatedByIdAndBudgetNull() {
        User user = User.builder()
                .name("Test User")
                .email("example@test.com")
                .password("password")
                .build();
        userRepository.save(user);

        UserSession.getInstance().setUser(user);

        Expense expense = new Expense();
        expense.setTitle("Test Expense");
        expense.setAmount(100.0);
        expense.setDate(new Date());
        expense.setDescription("Test Description");
        expense.setCategory(ExpenseCategory.OTHER);
        expenseRepository.save(expense);

        List<Expense> expenses = expenseRepository.findAllByCreatedByIdAndBudgetNull(user.getId());
        assertThat(expenses).isNotEmpty();

    }

    @Test
    void testFindByBudgetIdOrderByDateDesc() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();
        userRepository.save(user);

        UserSession.getInstance().setUser(user);

        Budget budget = new Budget();
        budget.setName("Test Budget");
        budget.setDescription("Test Description");
        budgetRepository.save(budget);

        Expense expense1 = new Expense();
        expense1.setTitle("Expense 1");
        expense1.setAmount(100.0);
        expense1.setDate(new Date(System.currentTimeMillis() - 100000));
        expense1.setCategory(ExpenseCategory.OTHER);
        expense1.setBudget(budget);
        expenseRepository.save(expense1);

        Expense expense2 = new Expense();
        expense2.setTitle("Expense 2");
        expense2.setAmount(200.0);
        expense2.setDate(new Date(System.currentTimeMillis() - 50000));
        expense2.setCategory(ExpenseCategory.OTHER);
        expense2.setBudget(budget);
        expenseRepository.save(expense2);

        List<Expense> expenses = expenseRepository.findByBudgetIdOrderByDateDesc(budget.getId(), Pageable.unpaged());
        assertThat(expenses).hasSize(2);
        assertThat(expenses.get(0).getTitle()).isEqualTo("Expense 1");
        assertThat(expenses.get(1).getTitle()).isEqualTo("Expense 2");
    }

    @Test
    void testFindByCreatedByIdAndBudgetNullOrderByDateDesc() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();
        userRepository.save(user);

        UserSession.getInstance().setUser(user);

        Expense expense1 = new Expense();
        expense1.setTitle("Expense 1");
        expense1.setAmount(100.0);
        expense1.setDate(new Date(System.currentTimeMillis() - 100000));
        expense1.setCategory(ExpenseCategory.OTHER);
        expense1.setCreatedBy(user);
        expenseRepository.save(expense1);

        Expense expense2 = new Expense();
        expense2.setTitle("Expense 2");
        expense2.setAmount(200.0);
        expense2.setDate(new Date(System.currentTimeMillis() - 50000));
        expense2.setCategory(ExpenseCategory.OTHER);
        expense2.setCreatedBy(user);
        expenseRepository.save(expense2);

        List<Expense> expenses = expenseRepository.findByCreatedByIdAndBudgetNullOrderByDateDesc(user.getId(), Pageable.unpaged());
        assertThat(expenses).hasSize(2);
        assertThat(expenses.get(0).getTitle()).isEqualTo("Expense 1");
        assertThat(expenses.get(1).getTitle()).isEqualTo("Expense 2");
    }

    @Test
    void testCountByCreatedByIdAndBudgetNull() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();
        userRepository.save(user);

        UserSession.getInstance().setUser(user);

        Expense expense = new Expense();
        expense.setTitle("Test Expense");
        expense.setAmount(100.0);
        expense.setDate(new Date());
        expense.setCategory(ExpenseCategory.OTHER);
        expense.setCreatedBy(user);
        expenseRepository.save(expense);

        int count = expenseRepository.countByCreatedByIdAndBudgetNull(user.getId());
        assertThat(count).isEqualTo(1);
    }

    @Test
    void testFindTopByCreatedByIdOrderByDateDesc() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();
        userRepository.save(user);

        UserSession.getInstance().setUser(user);

        Expense expense1 = new Expense();
        expense1.setTitle("Expense 1");
        expense1.setAmount(100.0);
        expense1.setDate(new Date(System.currentTimeMillis() - 100000));
        expense1.setCategory(ExpenseCategory.OTHER);
        expense1.setCreatedBy(user);
        expenseRepository.save(expense1);

        Expense expense2 = new Expense();
        expense2.setTitle("Expense 2");
        expense2.setAmount(200.0);
        expense2.setDate(new Date(System.currentTimeMillis() - 50000));
        expense2.setCategory(ExpenseCategory.OTHER);
        expense2.setCreatedBy(user);
        expenseRepository.save(expense2);

        List<Expense> expenses = expenseRepository.findTopByCreatedByIdOrderByDateDesc(user.getId(), Pageable.ofSize(1));
        assertThat(expenses).hasSize(1);
        assertThat(expenses.get(0).getTitle()).isEqualTo("Expense 1");
    }

}