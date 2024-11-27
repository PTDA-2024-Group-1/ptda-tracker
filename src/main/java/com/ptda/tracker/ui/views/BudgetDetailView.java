package com.ptda.tracker.ui.views;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.BudgetAccessLevel;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.services.tracker.BudgetAccessService;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.dialogs.ParticipantsDialog;
import com.ptda.tracker.ui.forms.BudgetForm;
import com.ptda.tracker.ui.forms.DistributeExpenseForm;
import com.ptda.tracker.ui.forms.ShareBudgetForm;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BudgetDetailView extends JPanel {
    private final MainFrame mainFrame;
    private final BudgetAccessService budgetAccessService;
    private final Budget budget;
    private final List<Expense> expenses;

    private JTable expensesTable;
    private JButton backButton, participantsButton, editButton, shareButton;

    public BudgetDetailView(MainFrame mainFrame, Budget budget) {
        this.mainFrame = mainFrame;
        budgetAccessService = mainFrame.getContext().getBean(BudgetAccessService.class);
        this.budget = budget;
        expenses = mainFrame.getContext().getBean(ExpenseService.class).getAllByBudgetId(budget.getId());

        initUI();
        setListeners();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Details Panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Budget Details"));

        JLabel nameLabel = new JLabel("Name: " + budget.getName());
        JLabel descriptionLabel = new JLabel("Description: " + budget.getDescription());
        JLabel createdByLabel = new JLabel("Created By: " + budget.getCreatedBy().getName());

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
        centerPanel.setBorder(BorderFactory.createTitledBorder("Expenses"));

        expensesTable = createExpensesTable();
        JScrollPane scrollPane = new JScrollPane(expensesTable);
        centerPanel.add(scrollPane);
        add(centerPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backButton = new JButton("Back to Budgets");
        buttonsPanel.add(backButton);

        participantsButton = new JButton("Participants");
        buttonsPanel.add(participantsButton);

        if (budgetAccessService.hasAccess(budget.getId(), UserSession.getInstance().getUser().getId(), BudgetAccessLevel.EDITOR)) {
            editButton = new JButton("Edit Budget");
            buttonsPanel.add(editButton);
        }
        if (budgetAccessService.hasAccess(budget.getId(), UserSession.getInstance().getUser().getId(), BudgetAccessLevel.EDITOR)) {
            shareButton = new JButton("Share Budget");
            buttonsPanel.add(shareButton);
        }
        if (budgetAccessService.hasAccess(budget.getId(), UserSession.getInstance().getUser().getId(), BudgetAccessLevel.OWNER)) {
            JButton distributeButton = new JButton("Distribute Expenses");
            distributeButton.addActionListener(e -> mainFrame.registerAndShowScreen(ScreenNames.DISTRIBUTE_EXPENSE_FORM, new DistributeExpenseForm(mainFrame, budget)));
            buttonsPanel.add(distributeButton);
        }
        // End Buttons Panel

        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private JTable createExpensesTable() {
        String[] columnNames = {"Title", "Amount", "Category", "Date", "Created By"};
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
        return new JTable(model);
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
}