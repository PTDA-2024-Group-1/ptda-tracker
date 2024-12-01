package com.ptda.tracker.ui.user.views;

import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.tracker.Subdivision;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.services.tracker.SubdivisionService;
import com.ptda.tracker.util.UserSession;

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
    private final SubdivisionService subdivisionService;
    private final Long budgetId;
    private User selectedUser;

    public ExpenseDetailsView(ExpenseService expenseService, SubdivisionService subdivisionService, Long budgetId) {
        this.expenseService = expenseService;
        this.subdivisionService = subdivisionService;
        this.budgetId = budgetId;
        initComponents();
    }

    private String[] getAllCategories() {
        Set<String> categories = new HashSet<>();
        categories.add("All");
        for (Expense expense : expenseService.getAllByBudgetId(budgetId)) {
            categories.add(expense.getCategory().toString());
        }
        return categories.toArray(new String[0]);
    }

    public void setSelectedUser(User user) {
        this.selectedUser = user;
        filterExpenses(); // Atualizar a tabela ao trocar de usuário
    }

    public void filterExpenses() {
        if (selectedUser == null) {
            return; // Não há usuário selecionado
        }

        String searchText = searchField.getText().toLowerCase();
        String selectedCategory = (String) categoryFilter.getSelectedItem();

        List<Expense> expenses = expenseService.getAllByBudgetId(budgetId);
        expenseTableModel.setRowCount(0);

        for (Expense expense : expenses) {
            List<Subdivision> subdivisions = subdivisionService.getAllByExpenseId(expense.getId());
            boolean userIsAssociated = subdivisions.stream()
                    .anyMatch(subdivision -> subdivision.getUser().getId().equals(selectedUser.getId()));

            if (!userIsAssociated) {
                continue;
            }

            boolean matchesSearch = expense.getTitle().toLowerCase().contains(searchText);
            boolean matchesCategory = selectedCategory.equals("All") ||
                    expense.getCategory().toString().equalsIgnoreCase(selectedCategory);

            if (matchesSearch && matchesCategory) {
                double userAmount = subdivisions.stream()
                        .filter(subdivision -> subdivision.getUser().getId().equals(selectedUser.getId()))
                        .mapToDouble(subdivision -> expense.getAmount() * (subdivision.getPercentage() / 100))
                        .sum();

                expenseTableModel.addRow(new Object[]{
                        expense.getTitle(),
                        "€" + String.format("%.2f", expense.getAmount()),
                        "€" + String.format("%.2f", userAmount),
                        expense.getCategory()
                });
            }
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel expenseDetailsTitle = new JLabel("My Expenses", SwingConstants.LEFT);
        expenseDetailsTitle.setFont(new Font("Arial", Font.BOLD, 16));
        add(expenseDetailsTitle, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(15);
        searchField.setToolTipText("Search by expense title");
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
        categoryFilter.setToolTipText("Filter by category");
        categoryFilter.addActionListener(e -> filterExpenses());

        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        filterPanel.add(new JLabel("Category:"));
        filterPanel.add(categoryFilter);

        add(filterPanel, BorderLayout.NORTH);

        expenseTableModel = new DefaultTableModel(new String[]{"Expense", "Total Cost", "Amount Paid", "Category"}, 0);
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

}