package com.ptda.tracker.ui.user.views;

import com.ptda.tracker.models.tracker.ExpenseDivision;
import com.ptda.tracker.models.tracker.BudgetAccess;
import com.ptda.tracker.models.tracker.BudgetAccessLevel;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.tracker.BudgetAccessService;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.services.tracker.ExpenseDivisionService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.forms.ExpenseForm;
import com.ptda.tracker.ui.user.forms.SubdivisionForm;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.ptda.tracker.ui.user.dialogs.ExpenseDivisionsDialog.createDivisionsJTable;

public class ExpenseDetailView extends JPanel {
    private final MainFrame mainFrame;
    private final ExpenseService expenseService;
    private final BudgetAccessService budgetAccessService;
    private Expense expense;
    private final List<ExpenseDivision> expenseDivisions;
    private final String returnScreen;
    private final Runnable onBack;

    public ExpenseDetailView(MainFrame mainFrame, Expense expense, String returnScreen, Runnable onBack) {
        this.mainFrame = mainFrame;
        this.expenseService = mainFrame.getContext().getBean(ExpenseService.class);
        this.budgetAccessService = mainFrame.getContext().getBean(BudgetAccessService.class);
        this.expense = expense;
        this.expenseDivisions = mainFrame.getContext().getBean(ExpenseDivisionService.class).getAllByExpenseId(expense.getId());
        this.returnScreen = returnScreen;
        this.onBack = onBack;

        initComponents();
        setValues(expense);
        setListeners();
    }

    private void setListeners() {
        backButton.addActionListener(e -> {
            if (onBack != null) onBack.run();
            if (expense != null && expense.getBudget() != null) {
                mainFrame.registerAndShowScreen(ScreenNames.BUDGET_DETAIL_VIEW, new BudgetDetailView(mainFrame, expense.getBudget()));
            } else {
                mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN);
            }
        });
        if (editButton != null) {
            editButton.addActionListener(e -> {
                ExpenseForm expenseForm = new ExpenseForm(mainFrame, expense, null, mainFrame.getCurrentScreen(), onBack);
                mainFrame.registerAndShowScreen(ScreenNames.EXPENSE_FORM, expenseForm);
            });
        }
        if (deleteButton != null) {
            deleteButton.addActionListener(e -> delete());
        }
        if (distributeDivisionExpenseButton != null) {
            distributeDivisionExpenseButton.addActionListener(e -> {
                SubdivisionForm expenseDivisionsOldFormRefactored = new SubdivisionForm(mainFrame, expense, expense.getBudget(), () -> mainFrame.registerAndShowScreen(ScreenNames.EXPENSE_DETAIL_VIEW, new ExpenseDetailView(mainFrame, expense, returnScreen, onBack)));
                mainFrame.registerAndShowScreen(ScreenNames.SUBDIVISION_FORM, expenseDivisionsOldFormRefactored);
            });
        }
    }

    private void delete() {
        if (expense.getId() == null) {
            JOptionPane.showMessageDialog(this, EXPENSE_NOT_FOUND, ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                DELETE_CONFIRMATION, DELETE_EXPENSE_TITLE, JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                expenseService.delete(expense.getId());
                if (onBack != null) {
                    onBack.run();
                }
                mainFrame.showScreen(returnScreen);
                JOptionPane.showMessageDialog(this, EXPENSE_DELETED_SUCCESS, SUCCESS, JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, DELETE_ERROR_MESSAGE + ": " + ex.getMessage(),
                        ERROR, JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void setValues(Expense expense) {
        nameValue.setText(expense.getTitle());
        amountValue.setText(String.valueOf(expense.getAmount()));
        categoryValue.setText(String.valueOf(expense.getCategory()));
        dateValue.setText(String.valueOf(expense.getDate()));
        createdByValue.setText(expense.getCreatedBy().getName());
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Expense Details Panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder(EXPENSE_DETAILS));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10); // Padding between elements
        gbc.anchor = GridBagConstraints.EAST; // Align labels to the right

        // Name Row
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1; // Allow labels to shrink/expand
        gbc.fill = GridBagConstraints.NONE;
        detailsPanel.add(new JLabel(NAME + ":"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST; // Align values to the left
        nameValue = new JLabel();
        detailsPanel.add(nameValue, gbc);

        // Amount Row
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        detailsPanel.add(new JLabel(AMOUNT + ":"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        amountValue = new JLabel();
        detailsPanel.add(amountValue, gbc);

        // Category Row
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        detailsPanel.add(new JLabel(CATEGORY + ":"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        categoryValue = new JLabel();
        detailsPanel.add(categoryValue, gbc);

        // Date Row
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        detailsPanel.add(new JLabel(DATE + ":"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        dateValue = new JLabel();
        detailsPanel.add(dateValue, gbc);

        // Created By Row
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        detailsPanel.add(new JLabel(CREATED_BY + ":"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        createdByValue = new JLabel();
        detailsPanel.add(createdByValue, gbc);

        // Position the details panel in the top-left corner
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(detailsPanel, BorderLayout.NORTH);
        add(wrapperPanel, BorderLayout.WEST);
        // End Expense Details Panel

        // Subdivisions Panel (Only show if expenseDivisions exist)
        if (!expenseDivisions.isEmpty()) {
            JPanel subdivisionsPanel = new JPanel();
            subdivisionsPanel.setLayout(new BoxLayout(subdivisionsPanel, BoxLayout.Y_AXIS));
            subdivisionsPanel.setBorder(BorderFactory.createTitledBorder(SUBDIVISIONS));
            subdivisionsTable = createSubdivisionsTable(expenseDivisions);
            subdivisionsPanel.add(new JScrollPane(subdivisionsTable));
            add(subdivisionsPanel, BorderLayout.EAST);
        }
        // End Subdivisions Panel

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new BorderLayout());

        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JButton(BACK);
        leftButtonPanel.add(backButton);

        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        editButton = new JButton(EDIT_EXPENSE);
        deleteButton = new JButton(DELETE_EXPENSE);
        auditButton = new JButton(AUDIT_EXPENSE);
        auditButton.addActionListener(e -> mainFrame.registerAndShowScreen(ScreenNames.EXPENSE_AUDIT_DETAIL_VIEW, new ExpenseAuditDetailView(mainFrame, expense)));

        rightButtonPanel.add(editButton);
        rightButtonPanel.add(deleteButton);
        rightButtonPanel.add(auditButton);

        if (expense.getBudget() != null) {
            User currentUser = UserSession.getInstance().getUser();
            BudgetAccess currentUserAccess = budgetAccessService.getAllByBudgetId(expense.getBudget().getId())
                    .stream()
                    .filter(access -> access.getUser().getId().equals(currentUser.getId()))
                    .findFirst()
                    .orElse(null);

            if (currentUserAccess == null || currentUserAccess.getAccessLevel() != BudgetAccessLevel.VIEWER) {
                distributeDivisionExpenseButton = new JButton(DISTRIBUTE_SUBDIVISIONS);
                distributeDivisionExpenseButton.addActionListener(e -> {
                    SubdivisionForm expenseDivisionsOldFormRefactored = new SubdivisionForm(mainFrame, expense, expense.getBudget(), () -> mainFrame.registerAndShowScreen(ScreenNames.EXPENSE_DETAIL_VIEW, new ExpenseDetailView(mainFrame, expense, returnScreen, onBack)));
                    mainFrame.registerAndShowScreen(ScreenNames.SUBDIVISION_FORM, expenseDivisionsOldFormRefactored);
                });
                rightButtonPanel.add(distributeDivisionExpenseButton);
            }
        }

        buttonsPanel.add(leftButtonPanel, BorderLayout.WEST);
        buttonsPanel.add(rightButtonPanel, BorderLayout.EAST);

        add(buttonsPanel, BorderLayout.SOUTH);
        // End Buttons Panel
    }

    private JTable createSubdivisionsTable(List<ExpenseDivision> expenseDivisions) {
        return createDivisionsJTable(expenseDivisions);
    }

    private JLabel nameLabel, amountLabel, categoryLabel, dateLabel, createdByLabel;
    private JLabel nameValue, amountValue, categoryValue, dateValue, createdByValue;
    private JTable subdivisionsTable;
    private JButton backButton, editButton, auditButton, deleteButton, distributeDivisionExpenseButton;

    private static final String
            EXPENSE_DETAILS = "Expense Details",
            NAME = "Name",
            AMOUNT = "Amount",
            CATEGORY = "Category",
            DATE = "Date",
            CREATED_BY = "Created By",
            SUBDIVISIONS = "Subdivisions",
            BACK = "Back",
            EDIT_EXPENSE = "Edit Expense",
            AUDIT_EXPENSE = "Audit Expense",
            DELETE_EXPENSE = "Delete Expense",
            DISTRIBUTE_SUBDIVISIONS = "Distribute Subdivisions",
            ERROR = "Error",
            SUCCESS = "Success",
            DELETE_CONFIRMATION = "Are you sure you want to delete this expense?",
            DELETE_EXPENSE_TITLE = "Delete Expense",
            EXPENSE_DELETED_SUCCESS = "Expense deleted successfully.",
            DELETE_ERROR_MESSAGE = "An error occurred while deleting the expense",
            EXPENSE_NOT_FOUND = "Expense not found.";
}