package com.ptda.tracker.services.administration;

import com.ptda.tracker.models.admin.Admin;
import com.ptda.tracker.models.assistance.Assistant;
import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.BudgetAccess;
import com.ptda.tracker.models.tracker.BudgetAccessLevel;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.assistance.AssistantService;
import com.ptda.tracker.services.tracker.BudgetAccessService;
import com.ptda.tracker.services.tracker.BudgetService;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.services.user.UserService;
import com.ptda.tracker.util.UserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DataGenerateServiceHibernateImpl implements DataGenerateService {

    private final UserService userService;
    private final AssistantService assistantService;
    private final AdminService adminService;
    private final BudgetService budgetService;
    private final BudgetAccessService budgetAccessService;
    private final ExpenseService expenseService;
    private List<User> users;
    private Map<String, String> credentials;

    @Override
    public String generateData() {
        System.out.println("Data Generate Service Started...");
        System.out.println("Creating users...");
        createUsers(20, 3,1);
        System.out.println("Creating budgets with expenses...");
        createBudgetsWithExpenses(10);
        System.out.println("Creating personal expenses...");
        createPersonalExpenses(50, 500);
        System.out.println("Data Generate Service Finished...");
        return returnCredentials();
    }

    private void createUsers(int usersCount, int assistantsCount, int adminsCount) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        users = new ArrayList<>();
        credentials = new HashMap<>();
        for (int i = 0; i < usersCount; i++) {
            User user = User.builder()
                    .name("User " + i)
                    .email("user" + System.currentTimeMillis() + Math.random() + "@gmail.com")
                    .password(passwordEncoder.encode("password"))
                    .build();
            users.add(user);
            credentials.put(user.getEmail(), "password");
        }
        users = userService.create(users);

        // Create assistants
        List<Assistant> assistants = new ArrayList<>();
        for (int i = 0; i < assistantsCount; i++) {
            Assistant assistant = new Assistant();
            assistant.setName("Assistant " + i);
            assistant.setEmail("assistant" + System.currentTimeMillis() + Math.random() + "@gmail.com");
            assistant.setPassword(passwordEncoder.encode("password"));
            assistants.add(assistant);
            credentials.put(assistant.getEmail(), "password");
        }
        assistantService.createAll(assistants);

        // Create admins
        List<Admin> admins = new ArrayList<>();
        for (int i = 0; i < adminsCount; i++) {
            Admin admin = new Admin();
            admin.setName("Admin " + i);
            admin.setEmail("admin" + System.currentTimeMillis() + Math.random() + "@gmail.com");
            admin.setPassword(passwordEncoder.encode("password"));
            admins.add(admin);
            credentials.put(admin.getEmail(), "password");
        }
        adminService.createAll(admins);
    }

    private void createBudgetsWithExpenses(int budgetsCount) {
        for (int i = 0; i < budgetsCount; i++) {
            Budget budget = Budget.builder()
                    .name("Budget " + i)
                    .description("Budget Description " + i)
                    .build();
            budget = budgetService.create(budget);

            // Create budget accesses for some users
            List<BudgetAccess> accesses = new ArrayList<>();
            for (User user : users) {
                if (Math.random() < 0.4) {
                    BudgetAccess access = BudgetAccess.builder()
                            .user(user)
                            .budget(budget)
                            .accessLevel(BudgetAccessLevel.values()[(int) (Math.random() * BudgetAccessLevel.values().length)])
                            .build();
                    accesses.add(access);
                }
            }
            accesses = budgetAccessService.createAll(accesses);

            // Delete budget access with id of current user for the created budget and select a random user from those who have access and make him the owner
            Long currentUserId = UserSession.getInstance().getUser().getId();
            budgetAccessService.deleteByBudgetIdAndUserId(budget.getId(), currentUserId);
            if (!accesses.isEmpty()) {
                BudgetAccess newOwnerAccess = accesses.get((int) (Math.random() * accesses.size()));
                newOwnerAccess.setAccessLevel(BudgetAccessLevel.OWNER);
                budgetAccessService.update(newOwnerAccess);
            }

            List<Expense> expenses = new ArrayList<>();
            for (BudgetAccess access : accesses) {
                List<Expense> userExpenses = generateExpenses(access.getUser(), budget, 10, 100);
                expenses.addAll(userExpenses);
            }
            expenseService.createAll(expenses);
        }
    }

    private List<Expense> generateExpenses(User user, Budget budget, int minExpenses, int maxExpenses) {
        List<Expense> expenses = new ArrayList<>();
        for (int i = 0; i < minExpenses + Math.random() * (maxExpenses - minExpenses); i++) {
            String title = (budget == null ? "Personal Expense " : "Expense ") + i;
            Expense expense = Expense.builder()
                    .title(title)
                    // 2 decimal cases
                    .amount((double) Math.round(Math.random() * 10000) / 100)
                    .description("Expense Description " + i)
                    .date(new Date(System.currentTimeMillis() - (long) (Math.random() * 10000000000L)))
                    .budget(budget)
                    .createdBy(user)
                    .build();
            expenses.add(expense);
        }
        return expenses;
    }

    private void createPersonalExpenses(int minExpenses, int maxExpenses) {
        List<Expense> expenses = new ArrayList<>();
        for (User user : users) {
            List<Expense> userExpenses = generateExpenses(user, null, minExpenses, maxExpenses);
            expenses.addAll(userExpenses);
        }
        expenseService.createAll(expenses);
    }

    String returnCredentials() {
        StringBuilder credentialsSB = new StringBuilder();
        credentials.forEach((email, password) -> {
            credentialsSB.append("Email: ").append(email).append("\nPassword: ").append(password).append("\n\n");
        });
        return credentialsSB.toString();
    }
}
