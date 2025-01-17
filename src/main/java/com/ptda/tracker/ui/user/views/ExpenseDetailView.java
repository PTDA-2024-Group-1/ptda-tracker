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
import com.ptda.tracker.ui.user.forms.DivisionsForm;
import com.ptda.tracker.ui.user.forms.ExpenseForm;
import com.ptda.tracker.util.LocaleManager;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

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
        distributeDivisions();
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
                DivisionsForm divisionsForm = new DivisionsForm(mainFrame, expense, mainFrame.getCurrentScreen(), this::onDistributeSuccess);
                mainFrame.registerAndShowScreen(ScreenNames.DIVISIONS_FORM, divisionsForm);
            });
        }
        auditButton.addActionListener(e -> mainFrame.registerAndShowScreen(
                ScreenNames.EXPENSE_AUDIT_DETAIL_VIEW,
                new ExpenseAuditListView(mainFrame, expense)
        ));
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

    private void onDistributeSuccess() {
        expenseDivisions.clear();
        expenseDivisions.addAll(mainFrame.getContext().getBean(ExpenseDivisionService.class).getAllByExpenseId(expense.getId()));
        divisionsTable.setModel(createSubdivisionsTable(expenseDivisions).getModel());
    }

    private void setValues(Expense expense) {
        nameValue.setText(expense.getTitle());
        amountValue.setText(String.valueOf(expense.getAmount()));
        categoryValue.setText(String.valueOf(expense.getCategory()));
        dateValue.setText(String.valueOf(expense.getDate()));
        createdByValue.setText(expense.getCreatedBy().getName());
    }

    private void distributeDivisions() {
        // Get participants
        List<User> participants = mainFrame.getContext().getBean(BudgetAccessService.class)
                .getAllByBudgetId(expense.getBudget().getId())
                .stream()
                .map(BudgetAccess::getUser)
                .toList();

        // Total amounts
        double totalAssignedAmount = expenseDivisions.stream()
                .mapToDouble(ExpenseDivision::getAmount)
                .sum();
        double remainingAmount = expense.getAmount() - totalAssignedAmount;

        // Identify creator of the expense
        User creator = expense.getCreatedBy();

        // Total paid amounts
        double totalPaidAmount = expenseDivisions.stream()
                .mapToDouble(ExpenseDivision::getPaidAmount)
                .sum();
        double remainingPaid = expense.getAmount() - totalPaidAmount;

        // Generate missing divisions
        for (User participant : participants) {
            boolean divisionExists = expenseDivisions.stream()
                    .anyMatch(division -> division.getUser().getId().equals(participant.getId()));

            if (!divisionExists) {
                ExpenseDivision newDivision = new ExpenseDivision();
                newDivision.setUser(participant);
                newDivision.setExpense(expense);
                newDivision.setEqualDivision(true); // Default to equal division
                newDivision.setPaidAmount(0); // Default paid amount
                newDivision.setAmount(0); // Default amount
                expenseDivisions.add(newDivision);
            }
        }

        // Assign remaining paid amount to creator if not already specified
        Optional<ExpenseDivision> creatorDivision = expenseDivisions.stream()
                .filter(division -> division.getUser().getId().equals(creator.getId()))
                .findFirst();

        if (creatorDivision.isPresent() && creatorDivision.get().getPaidAmount() == 0) {
            creatorDivision.get().setPaidAmount(remainingPaid);
            remainingPaid = 0; // Reset remaining paid as it's fully allocated
        }

        // Distribute remaining amounts among equal divisions
        long equalDivisionCount = expenseDivisions.stream()
                .filter(ExpenseDivision::isEqualDivision)
                .count();

        if (equalDivisionCount > 0) {
            if (remainingAmount > 0) {
                double perDivisionAmount = remainingAmount / equalDivisionCount;
                for (ExpenseDivision division : expenseDivisions) {
                    if (division.isEqualDivision()) {
                        division.setAmount(division.getAmount() + perDivisionAmount);
                    }
                }
            }
            if (remainingPaid > 0) {
                double perDivisionPaid = remainingPaid / equalDivisionCount;
                for (ExpenseDivision division : expenseDivisions) {
                    if (division.isEqualDivision()) {
                        division.setPaidAmount(division.getPaidAmount() + perDivisionPaid);
                    }
                }
            }
        }

        // Display the subdivisions panel
        if (!expenseDivisions.isEmpty()) {
            JPanel subdivisionsPanel = new JPanel();
            subdivisionsPanel.setLayout(new BoxLayout(subdivisionsPanel, BoxLayout.Y_AXIS));
            subdivisionsPanel.setBorder(BorderFactory.createTitledBorder(DIVISIONS));
            divisionsTable = createSubdivisionsTable(expenseDivisions);
            subdivisionsPanel.add(new JScrollPane(divisionsTable));
            add(subdivisionsPanel, BorderLayout.EAST);
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Expense Details Panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder(EXPENSE_DETAILS));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10); // Padding between elements
        gbc.anchor = GridBagConstraints.WEST;

        // Name Row
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1; // Allow labels to shrink/expand
        detailsPanel.add(new JLabel(NAME + ":"), gbc);

        gbc.gridx = 1;
        nameValue = new JLabel();
        detailsPanel.add(nameValue, gbc);

        // Amount Row
        gbc.gridx = 0;
        gbc.gridy = 1;
        detailsPanel.add(new JLabel(AMOUNT + ":"), gbc);

        gbc.gridx = 1;
        amountValue = new JLabel();
        detailsPanel.add(amountValue, gbc);

        // Category Row
        gbc.gridx = 0;
        gbc.gridy = 2;
        detailsPanel.add(new JLabel(CATEGORY + ":"), gbc);

        gbc.gridx = 1;
        categoryValue = new JLabel();
        detailsPanel.add(categoryValue, gbc);

        // Date Row
        gbc.gridx = 0;
        gbc.gridy = 3;
        detailsPanel.add(new JLabel(DATE + ":"), gbc);

        gbc.gridx = 1;
        dateValue = new JLabel();
        detailsPanel.add(dateValue, gbc);

        // Created By Row
        gbc.gridx = 0;
        gbc.gridy = 4;
        detailsPanel.add(new JLabel(CREATED_BY + ":"), gbc);

        gbc.gridx = 1;
        createdByValue = new JLabel();
        detailsPanel.add(createdByValue, gbc);

        // Position the details panel in the top-left corner
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(detailsPanel, BorderLayout.NORTH);
        add(wrapperPanel, BorderLayout.WEST);
        // End Expense Details Panel

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new BorderLayout());

        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JButton(BACK);
        leftButtonPanel.add(backButton);

        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        auditButton = new JButton(ACTIVITY);
        rightButtonPanel.add(auditButton);
        editButton = new JButton(EDIT_EXPENSE);
        deleteButton = new JButton(DELETE_EXPENSE);
        rightButtonPanel.add(editButton);
        rightButtonPanel.add(deleteButton);

        if (expense.getBudget() != null) {
            User currentUser = UserSession.getInstance().getUser();
            BudgetAccess currentUserAccess = budgetAccessService.getAllByBudgetId(expense.getBudget().getId())
                    .stream()
                    .filter(access -> access.getUser().getId().equals(currentUser.getId()))
                    .findFirst()
                    .orElse(null);
            distributeDivisionExpenseButton = new JButton(DISTRIBUTE_DIVISIONS);
            distributeDivisionExpenseButton = new JButton(DISTRIBUTE_DIVISIONS);
            rightButtonPanel.add(distributeDivisionExpenseButton);
            if (currentUserAccess != null && currentUserAccess.getAccessLevel() == BudgetAccessLevel.VIEWER) {
                editButton.setVisible(false);
                deleteButton.setVisible(false);
                distributeDivisionExpenseButton.setVisible(false);
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

    private JLabel nameValue, amountValue, categoryValue, dateValue, createdByValue;
    private JTable divisionsTable;
    private JButton backButton, editButton, auditButton, deleteButton, distributeDivisionExpenseButton;

    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            EXPENSE_DETAILS = localeManager.getTranslation("expense_details"),
            NAME = localeManager.getTranslation("name"),
            AMOUNT = localeManager.getTranslation("amount"),
            CATEGORY = localeManager.getTranslation("category"),
            DATE = localeManager.getTranslation("date"),
            CREATED_BY = localeManager.getTranslation("created_by"),
            DIVISIONS = localeManager.getTranslation("divisions"),
            BACK = localeManager.getTranslation("back"),
            EDIT_EXPENSE = localeManager.getTranslation("edit_expense"),
            ACTIVITY = localeManager.getTranslation("activity"),
            DELETE_EXPENSE = localeManager.getTranslation("delete_expense"),
            DISTRIBUTE_DIVISIONS = localeManager.getTranslation("distribute_divisions"),
            ERROR = localeManager.getTranslation("error"),
            SUCCESS = localeManager.getTranslation("success"),
            DELETE_CONFIRMATION = localeManager.getTranslation("delete_confirmation"),
            DELETE_EXPENSE_TITLE = localeManager.getTranslation("delete_expense_title"),
            EXPENSE_DELETED_SUCCESS = localeManager.getTranslation("expense_deleted_success"),
            DELETE_ERROR_MESSAGE = localeManager.getTranslation("delete_error_message"),
            EXPENSE_NOT_FOUND = localeManager.getTranslation("expense_not_found");
}