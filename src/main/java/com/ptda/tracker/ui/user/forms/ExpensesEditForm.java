package com.ptda.tracker.ui.user.forms;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.tracker.ExpenseCategory;
import com.ptda.tracker.services.tracker.BudgetService;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.components.cellEditors.BudgetDropdownCellEditor;
import com.ptda.tracker.ui.user.components.cellEditors.DatePickerCellEditor;
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
    private final List<Expense> expenses;
    private final Budget defaultBudget;
    private final String returnScreen;
    private final Runnable onImportSuccess;

    public ExpensesEditForm(MainFrame mainFrame, List<Expense> expenses,
                            Budget defaultBudget, String returnScreen, Runnable onImportSuccess) {
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

    private void setListeners() {
        backButton.addActionListener(e -> mainFrame.showScreen(returnScreen));
        applyBudgetButton.addActionListener(e -> applyBudget());
        submitButton.addActionListener(e -> submitExpenses());
    }

    private void applyBudget() {
        String selectedBudgetName = (String) budgetsDropdown.getSelectedItem();
        Budget selectedBudget = budgets.stream()
                .filter(b -> b.getName().equals(selectedBudgetName))
                .findFirst()
                .orElse(defaultBudget);

        for (Expense expense : expenses) {
            expense.setBudget(selectedBudget);
        }
        expensesTable.repaint();
    }

    private void submitExpenses() {
        // Split expenses into "to create" and "to update" lists
        List<Expense> updatedExpenses = ((ExpensesTableModel) expensesTable.getModel()).getExpenses();
        List<Expense> toCreate = updatedExpenses.stream()
                .filter(expense -> expense.getId() == null) // Assuming null ID means the expense is new
                .collect(Collectors.toList());
        List<Expense> toUpdate = updatedExpenses.stream()
                .filter(expense -> expense.getId() != null) // Non-null ID means the expense exists
                .collect(Collectors.toList());

        // Call the appropriate services
        ExpenseService expenseService = mainFrame.getContext().getBean(ExpenseService.class);
        if (!toCreate.isEmpty()) {
            expenseService.createAll(toCreate);
        }
        if (!toUpdate.isEmpty()) {
            expenseService.updateAll(toUpdate);
        }

        // Notify success and navigate back
        onImportSuccess.run();
        mainFrame.showScreen(returnScreen);
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

    private void initComponents() {
        setLayout(new BorderLayout());

        // Table model and setup
        ExpensesTableModel tableModel = new ExpensesTableModel(expenses, budgets);
        expensesTable = new JXTable(tableModel);

        // Set custom editors
        expensesTable.getColumnModel().getColumn(2)
                .setCellEditor(new DatePickerCellEditor());

        // Use ExpenseCategory values
        JComboBox<ExpenseCategory> categoryComboBox = new JComboBox<>(ExpenseCategory.values());
        expensesTable.getColumnModel().getColumn(3)
                .setCellEditor(new DefaultCellEditor(categoryComboBox));

        expensesTable.getColumnModel().getColumn(5)
                .setCellEditor(new BudgetDropdownCellEditor(budgets));

        // Budget map for display
        Map<String, Budget> budgetMap = createBudgetMap(budgets);

        // Apply budget button and dropdown
        applyBudgetButton = new JButton(APPLY_BUDGET);
        budgetsDropdown = new JComboBox<>(budgetMap.keySet().toArray(new String[0]));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel(DEFAULT_BUDGET + ":"));
        topPanel.add(budgetsDropdown);
        topPanel.add(applyBudgetButton);

        // Submit and back buttons
        submitButton = new JButton(SUBMIT);
        backButton = new JButton(CANCEL);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(backButton);
        buttonPanel.add(submitButton);

        // Add components to the panel
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(expensesTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JXTable expensesTable;
    private JButton submitButton, backButton, applyBudgetButton;
    private JComboBox<String> budgetsDropdown;
    private static final String
            APPLY_BUDGET = "Apply Budget to All",
            DEFAULT_BUDGET = "Default Budget",
            CANCEL = "Cancel",
            SUBMIT = "Submit";
}
