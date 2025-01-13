package com.ptda.tracker.ui.user.views;

import com.ptda.tracker.models.tracker.BudgetAccess;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.tracker.ExpenseDivision;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.tracker.BudgetAccessService;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.services.tracker.ExpenseDivisionService;
import com.ptda.tracker.util.LocaleManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class ExpenseDetailsView extends JPanel {
    private final ExpenseService expenseService;
    private final ExpenseDivisionService expenseDivisionService;
    private final BudgetAccessService budgetAccessService;
    private final Long budgetId;
    private User selectedUser;

    public ExpenseDetailsView(ExpenseService expenseService, ExpenseDivisionService expenseDivisionService, BudgetAccessService budgetAccessService, Long budgetId) {
        this.expenseService = expenseService;
        this.expenseDivisionService = expenseDivisionService;
        this.budgetAccessService = budgetAccessService;
        this.budgetId = budgetId;
        initComponents();
    }

    private String[] getAllCategories() {
        Set<String> categories = new HashSet<>();
        categories.add(ALL);
        for (Expense expense : expenseService.getAllByBudgetId(budgetId)) {
            categories.add(expense.getCategory().toString());
        }
        return categories.toArray(new String[0]);
    }

    public void setSelectedUser(User user) {
        this.selectedUser = user;
        filterExpenses(); // Update the table when changing the user
    }

    private void filterExpenses() {
        if (selectedUser == null) {
            return;
        }

        String searchText = searchField.getText().toLowerCase();
        String selectedCategory = (String) categoryFilter.getSelectedItem();

        List<Expense> expenses = expenseService.getAllByBudgetId(budgetId);
        expenseTableModel.setRowCount(0);

        List<BudgetAccess> budgetAccesses = budgetAccessService.getAllByBudgetId(budgetId);
        List<User> users = budgetAccesses.stream()
                .map(BudgetAccess::getUser)
                .toList();

        Set<Long> displayedExpenses = new HashSet<>();

        for (Expense expense : expenses) {
            List<ExpenseDivision> expenseDivisions = expenseDivisionService.getAllByExpenseId(expense.getId());

            double userAmount = expenseDivisions.stream()
                    .filter(subdivision -> subdivision.getUser().getId().equals(selectedUser.getId()))
                    .mapToDouble(ExpenseDivision::getPaidAmount)
                    .sum();

            boolean userIsAssociated = expenseDivisions.stream()
                    .anyMatch(subdivision -> subdivision.getUser().getId().equals(selectedUser.getId())) || expenseDivisions.isEmpty();

            if (expenseDivisions.isEmpty() && !displayedExpenses.contains(expense.getId())) {
                userAmount = expense.getAmount() / users.size();
                userIsAssociated = true;
            }

            boolean matchesSearch = expense.getTitle().toLowerCase().contains(searchText);
            boolean matchesCategory = selectedCategory.equals("All") ||
                    expense.getCategory().toString().equalsIgnoreCase(selectedCategory);

            if (matchesSearch && matchesCategory && userIsAssociated) {
                expenseTableModel.addRow(new Object[]{
                        expense.getTitle(),
                        "€" + String.format("%.2f", expense.getAmount()),
                        "€" + String.format("%.2f", userAmount),
                        expense.getCategory()
                });
                displayedExpenses.add(expense.getId());
            }
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel expenseDetailsTitle = new JLabel(MY_EXPENSES, SwingConstants.LEFT);
        expenseDetailsTitle.setFont(new Font("Arial", Font.BOLD, 16));
        add(expenseDetailsTitle, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(15);
        searchField.setToolTipText(SEARCH);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterExpenses();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterExpenses();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterExpenses();
            }
        });

        categoryFilter = new JComboBox<>(getAllCategories());
        categoryFilter.setToolTipText(FILTER_CATEGORY);
        categoryFilter.addActionListener(e -> filterExpenses());

        filterPanel.add(new JLabel(SEARCH_SIMPLE));
        filterPanel.add(searchField);
        filterPanel.add(new JLabel(CATEGORY_SEARCH));
        filterPanel.add(categoryFilter);

        add(filterPanel, BorderLayout.NORTH);

        expenseTableModel = new DefaultTableModel(new String[]{EXPENSE, TOTAL_COST, AMOUNT_PAID, CATEGORY}, 0);
        expenseTable = new JTable(expenseTableModel);
        expenseTable.setFillsViewportHeight(true);
        expenseTable.setDefaultEditor(Object.class, null);

        JScrollPane scrollPane = new JScrollPane(expenseTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private DefaultTableModel expenseTableModel;
    private JTextField searchField;
    private JComboBox<String> categoryFilter;
    private JTable expenseTable;
    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            ALL = localeManager.getTranslation("all"),
            MY_EXPENSES = localeManager.getTranslation("my_expenses"),
            SEARCH = localeManager.getTranslation("search"),
            SEARCH_SIMPLE = localeManager.getTranslation("search_simple"),
            CATEGORY_SEARCH = localeManager.getTranslation("category_search"),
            FILTER_CATEGORY = localeManager.getTranslation("filter_category"),
            EXPENSE = localeManager.getTranslation("expense"),
            TOTAL_COST = localeManager.getTranslation("total_cost"),
            AMOUNT_PAID = localeManager.getTranslation("amount_paid"),
            CATEGORY = localeManager.getTranslation("category");
}