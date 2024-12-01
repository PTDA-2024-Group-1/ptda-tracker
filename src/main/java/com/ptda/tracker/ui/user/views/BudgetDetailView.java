package com.ptda.tracker.ui.user.views;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.BudgetAccessLevel;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.tracker.BudgetAccessService;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.dialogs.ParticipantsDialog;
import com.ptda.tracker.ui.user.forms.BudgetForm;
import com.ptda.tracker.ui.user.forms.ExpenseForm;
import com.ptda.tracker.ui.user.forms.ShareBudgetForm;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BudgetDetailView extends JPanel {
    private final MainFrame mainFrame;
    private final BudgetAccessService budgetAccessService;
    private final User user = UserSession.getInstance().getUser();
    private final Budget budget;
    private final List<Expense> expenses;

    public BudgetDetailView(MainFrame mainFrame, Budget budget) {
        this.mainFrame = mainFrame;
        budgetAccessService = mainFrame.getContext().getBean(BudgetAccessService.class);
        this.budget = budget;
        expenses = mainFrame.getContext().getBean(ExpenseService.class).getAllByBudgetId(budget.getId());

        initComponents();
        setListeners();
    }

    private void setListeners() {
        backButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN));
        participantsButton.addActionListener(e -> {
            ParticipantsDialog participantsDialog = new ParticipantsDialog(mainFrame, budget);

            participantsDialog.setVisible(true);
        });
        if (editButton != null) {
            editButton.addActionListener(e -> mainFrame.registerAndShowScreen(ScreenNames.BUDGET_FORM, new BudgetForm(mainFrame, null, budget)));
        }
        if (shareButton != null) {
            shareButton.addActionListener(e -> mainFrame.registerAndShowScreen(ScreenNames.BUDGET_SHARE_FORM, new ShareBudgetForm(mainFrame, budget)));
        }
        if (addExpenseButton != null) {
            addExpenseButton.addActionListener(e -> mainFrame.registerAndShowScreen(ScreenNames.EXPENSE_FORM, new ExpenseForm(mainFrame, null, budget, mainFrame.getCurrentScreen(), this::refreshExpenses)));
        }
        expensesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = expensesTable.getSelectedRow();
                if (selectedRow != -1) {
                    Expense selectedExpense = expenses.get(selectedRow);
                    mainFrame.registerAndShowScreen(ScreenNames.EXPENSE_DETAIL_VIEW, new ExpenseDetailView(mainFrame, selectedExpense, mainFrame.getCurrentScreen(), null));
                    expensesTable.clearSelection();
                }
            }
        });
    }

    private void refreshExpenses() {
        expenses.clear();
        expenses.addAll(mainFrame.getContext().getBean(ExpenseService.class).getAllByBudgetId(budget.getId()));
        expensesTable.setModel(createExpensesTableModel());
    }

    private DefaultTableModel createExpensesTableModel() {
        String[] columnNames = {TITLE, AMOUNT, CATEGORY, DATE, CREATED_BY_COLUMN};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (Expense expense : expenses) {
            model.addRow(new Object[]{
                    expense.getTitle(),
                    expense.getAmount(),
                    expense.getCategory(),
                    expense.getDate(),
                    expense.getCreatedBy().getName()});
        }
        return model;
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Details Panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createTitledBorder(BUDGET_DETAILS));

        nameLabel = new JLabel(NAME + ": " + budget.getName());
        descriptionLabel = new JLabel(DESCRIPTION + ": " + budget.getDescription());
        createdByLabel = new JLabel(CREATED_BY + ": " + budget.getCreatedBy().getName());

        Font font = new Font("Arial", Font.PLAIN, 14);
        nameLabel.setFont(font);
        descriptionLabel.setFont(font);
        createdByLabel.setFont(font);

        detailsPanel.add(nameLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(descriptionLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(createdByLabel);
        add(detailsPanel, BorderLayout.NORTH);

        // Central Panel
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        centerPanel.setBorder(BorderFactory.createTitledBorder(EXPENSES));

        expensesTable = new JTable(createExpensesTableModel());
        JScrollPane scrollPane = new JScrollPane(expensesTable);
        centerPanel.add(scrollPane);
        add(centerPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backButton = new JButton(BACK);
        buttonsPanel.add(backButton);

        participantsButton = new JButton(PARTICIPANTS);
        buttonsPanel.add(participantsButton);

        boolean hasOwnerAccess = budgetAccessService.hasAccess(budget.getId(), user.getId(), BudgetAccessLevel.OWNER);
        boolean hasEditorAccess = budgetAccessService.hasAccess(budget.getId(), user.getId(), BudgetAccessLevel.EDITOR);

        if (hasEditorAccess) {
            editButton = new JButton(EDIT_BUDGET);
            buttonsPanel.add(editButton);
        }
        if (hasOwnerAccess) {
            shareButton = new JButton(SHARE_BUDGET);
            buttonsPanel.add(shareButton);
        }
        if (hasEditorAccess) {
            addExpenseButton = new JButton(ADD_EXPENSE);
            buttonsPanel.add(addExpenseButton);
        }
        if (!expenses.isEmpty()) {
            JButton simulateBudget = new JButton(SIMULATION_BUDGET);
            simulateBudget.addActionListener(e -> mainFrame.registerAndShowScreen(ScreenNames.SIMULATE_VIEW, new SimulationView(mainFrame, budget)));
            buttonsPanel.add(simulateBudget);
        }
        // End Buttons Panel

        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private JTable expensesTable;
    JLabel nameLabel, descriptionLabel, createdByLabel;
    private JButton backButton, participantsButton, editButton, shareButton, addExpenseButton;
    private static final String
            BUDGET_DETAILS = "Budget Details",
            NAME = "Name",
            DESCRIPTION = "Description",
            CREATED_BY = "Created By",
            EXPENSES = "Expenses",
            BACK = "Back",
            PARTICIPANTS = "Participants",
            EDIT_BUDGET = "Edit Budget",
            ADD_EXPENSE = "Add Expense",
            SHARE_BUDGET = "Share Budget",
            SIMULATION_BUDGET = "Simulation Budget",
            TITLE = "Title",
            AMOUNT = "Amount",
            CATEGORY = "Category",
            DATE = "Date",
            CREATED_BY_COLUMN = "Created By";
}