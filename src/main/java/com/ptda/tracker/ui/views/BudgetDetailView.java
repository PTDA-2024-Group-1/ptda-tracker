package com.ptda.tracker.ui.views;

import com.ptda.tracker.models.dispute.Subdivision;
import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.BudgetAccess;
import com.ptda.tracker.models.tracker.BudgetAccessLevel;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.tracker.SubdivisionService;
import com.ptda.tracker.services.tracker.BudgetAccessService;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.ui.MainFrame;
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
    private final BudgetAccessService budgetAccessService;
    private final ExpenseService expenseService;
    private final SubdivisionService subdivisionService;
    private final Budget budget;

    public BudgetDetailView(MainFrame mainFrame, Budget budget) {
        this.budget = budget;
        budgetAccessService = mainFrame.getContext().getBean(BudgetAccessService.class);
        expenseService = mainFrame.getContext().getBean(ExpenseService.class);
        subdivisionService = mainFrame.getContext().getBean(SubdivisionService.class);

        setLayout(new BorderLayout(15, 15)); // Espaçamento entre elementos
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Margem externa

        // Painel de detalhes do orçamento
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

        // Painel central com tabelas
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        // Tabela de participantes
        JTable participantsTable = createParticipantsTable(budget.getId());
        JScrollPane participantsScrollPane = new JScrollPane(participantsTable);
        participantsScrollPane.setBorder(BorderFactory.createTitledBorder("Participants"));
        centerPanel.add(participantsScrollPane);

        // Tabela de subdivisões
        JTable subdivisionsTable = createSubdivisionsTable(budget.getId());
        JScrollPane subdivisionsScrollPane = new JScrollPane(subdivisionsTable);
        subdivisionsScrollPane.setBorder(BorderFactory.createTitledBorder("Subdivisions"));
        centerPanel.add(subdivisionsScrollPane);

        add(centerPanel, BorderLayout.CENTER);

        // Painel de botões
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back to Budgets");
        JButton editButton = new JButton("Edit Budget");
        JButton shareButton = new JButton("Share Budget");

        backButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN));
        editButton.addActionListener(e -> mainFrame.registerAndShowScreen(ScreenNames.BUDGET_FORM, new BudgetForm(mainFrame, null, budget)));
        shareButton.addActionListener(e -> mainFrame.registerAndShowScreen(ScreenNames.BUDGET_SHARE_FORM, new ShareBudgetForm(mainFrame, budget)));

        buttonsPanel.add(backButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(shareButton);

        if (userHasAccessLevel(BudgetAccessLevel.OWNER, BudgetAccessLevel.EDITOR)) {
            JButton distributeButton = new JButton("Distribute Expenses");
            distributeButton.addActionListener(e -> mainFrame.registerAndShowScreen(ScreenNames.DISTRIBUTE_EXPENSE_FORM, new DistributeExpenseForm(mainFrame, budget)));
            buttonsPanel.add(distributeButton);
        }

        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private boolean userHasAccessLevel(BudgetAccessLevel... levels) {
        User currentUser = UserSession.getInstance().getUser();
        List<BudgetAccess> accesses = budgetAccessService.getAllByBudgetId(budget.getId());
        for (BudgetAccess access : accesses) {
            if (access.getUser().equals(currentUser)) {
                for (BudgetAccessLevel level : levels) {
                    if (access.getAccessLevel() == level) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private JTable createParticipantsTable(Long budgetId) {
        List<BudgetAccess> accesses = budgetAccessService.getAllByBudgetId(budgetId);
        String[] columnNames = {"Name", "Email", "Access Level"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (BudgetAccess access : accesses) {
            model.addRow(new Object[]{access.getUser().getName(), access.getUser().getEmail(), access.getAccessLevel().toString()});
        }

        JTable table = new JTable(model);
        table.setEnabled(false);
        return table;
    }

    private JTable createSubdivisionsTable(Long budgetId) {
        List<Expense> expenses = expenseService.getAllByBudgetId(budgetId);
        String[] columnNames = {"Expense", "User", "Amount", "Percentage"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Expense expense : expenses) {
            List<Subdivision> subdivisions = subdivisionService.getAllByExpenseId(expense.getId());
            for (Subdivision subdivision : subdivisions) {
                String percentageWithSign = subdivision.getPercentage() + "%";
                model.addRow(new Object[]{subdivision.getExpense(), subdivision.getUser().getName(), subdivision.getAmount(), percentageWithSign});
            }
        }

        JTable table = new JTable(model);
        table.setEnabled(false);
        return table;
    }

}