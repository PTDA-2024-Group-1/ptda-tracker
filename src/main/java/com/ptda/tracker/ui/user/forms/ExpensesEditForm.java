package com.ptda.tracker.ui.user.forms;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.tracker.ExpenseCategory;
import com.ptda.tracker.services.tracker.BudgetService;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.components.cellEditors.BudgetDropdownEditor;
import com.ptda.tracker.ui.user.components.cellEditors.DatePickerEditor;
import com.ptda.tracker.ui.user.components.tables.ExpensesTableModel;
import com.ptda.tracker.util.UserSession;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExpensesEditForm extends JPanel {
    private final MainFrame mainFrame;
    private final List<Budget> budgets;
    private final Budget defaultBudget;
    private final String returnScreen;
    private final Runnable onImportSuccess;

    private JXTable expensesTable;
    private JButton submitButton, backButton, applyBudgetButton;
    private JComboBox<String> budgetsDropdown;
    private final List<Expense> expenses;

    public ExpensesEditForm(MainFrame mainFrame, List<Expense> expenses, Budget defaultBudget, String returnScreen, Runnable onImportSuccess) {
        this.mainFrame = mainFrame;
        budgets = mainFrame.getContext().getBean(BudgetService.class)
                .getAllByUserId(UserSession.getInstance().getUser().getId());
        this.returnScreen = returnScreen;
        this.expenses = expenses;
        this.defaultBudget = defaultBudget;
        this.onImportSuccess = onImportSuccess;

        initComponents();
        setListeners();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Table model and setup
        ExpensesTableModel tableModel = new ExpensesTableModel(expenses, budgets);
        expensesTable = new JXTable(tableModel);

        // Set custom editors
        expensesTable.getColumnModel().getColumn(2).setCellEditor(new DatePickerEditor()); // Date column
        expensesTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JComboBox<>(ExpenseCategory.values()))); // Category dropdown
        expensesTable.getColumnModel().getColumn(5).setCellEditor(new BudgetDropdownEditor(budgets)); // Budget dropdown

        // Budget map for display
        Map<String, Budget> budgetMap = createBudgetMap(budgets);

        // Apply budget button and dropdown
        applyBudgetButton = new JButton("Apply Budget to All");
        budgetsDropdown = new JComboBox<>(budgetMap.keySet().toArray(new String[0]));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Default Budget:"));
        topPanel.add(budgetsDropdown);
        topPanel.add(applyBudgetButton);

        // Submit and back buttons
        submitButton = new JButton("Submit");
        backButton = new JButton("Back");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(backButton);
        buttonPanel.add(submitButton);

        // Add components to the panel
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(expensesTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setListeners() {
        backButton.addActionListener(e -> mainFrame.showScreen(returnScreen));

        applyBudgetButton.addActionListener(e -> {
            String selectedBudgetName = (String) budgetsDropdown.getSelectedItem();
            Budget selectedBudget = budgets.stream()
                    .filter(b -> b.getName().equals(selectedBudgetName))
                    .findFirst()
                    .orElse(defaultBudget);

            for (Expense expense : expenses) {
                expense.setBudget(selectedBudget);
            }
            expensesTable.repaint();
        });

        submitButton.addActionListener(e -> {
            List<Expense> updatedExpenses = ((ExpensesTableModel) expensesTable.getModel()).getExpenses();
            mainFrame.getContext().getBean(ExpenseService.class).createAll(updatedExpenses);

            onImportSuccess.run();
            mainFrame.showScreen(returnScreen);
        });
    }

    public static Map<String, Budget> createBudgetMap(List<Budget> budgets) {
        Map<String, Budget> budgetMap = new HashMap<>();
        Map<String, Long> nameCounts = budgets.stream()
                .collect(Collectors.groupingBy(Budget::getName, Collectors.counting()));

        for (Budget budget : budgets) {
            String key = budget.getName();
            if (nameCounts.get(key) > 1) {
                key += " (id: " + budget.getId() + ")";
            }
            budgetMap.put(key, budget);
        }

        return budgetMap;
    }
}
