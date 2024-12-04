package com.ptda.tracker.ui.user.views;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.services.tracker.BudgetAuditService;
import com.ptda.tracker.services.tracker.BudgetService;
import com.ptda.tracker.services.tracker.ExpenseAuditService;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.ScreenNames;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BudgetAuditDetailView extends JPanel {
    private final MainFrame mainFrame;
    private final Budget budget;
    private final BudgetAuditService budgetAuditService;
    private final ExpenseAuditService expenseAuditService;
    private final ExpenseService expenseService;

    public BudgetAuditDetailView(MainFrame mainFrame, Budget budget) {
        this.mainFrame = mainFrame;
        this.budget = budget;
        this.budgetAuditService = mainFrame.getContext().getBean(BudgetAuditService.class);
        this.expenseAuditService = mainFrame.getContext().getBean(ExpenseAuditService.class);
        this.expenseService = mainFrame.getContext().getBean(ExpenseService.class);

        initComponents();
        populateAuditDetails();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Audit Details", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        auditDetailsArea = new JTextArea();
        auditDetailsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(auditDetailsArea);
        add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.BUDGET_DETAIL_VIEW));
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footerPanel.add(backButton);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void populateAuditDetails() {
        StringBuilder auditInfo = new StringBuilder();

        // Fetch and append budget audit details
        List<Object[]> budgetAuditDetails = budgetAuditService.getBudgetRevisionsWithDetails(budget.getId());
        for (Object[] detail : budgetAuditDetails) {
            auditInfo.append("Budget: ").append(detail[0]).append(" ").append(detail[1]).append("\n");
        }

        // Fetch and append expense audit details using BudgetService
        List<Expense> expenses = expenseService.getAllByBudgetId(budget.getId());
        for (Expense expense : expenses) {
            List<Object[]> expenseAuditDetails = expenseAuditService.getExpenseRevisionsWithDetails(expense.getId());
            for (Object[] detail : expenseAuditDetails) {
                auditInfo.append("Expense: ").append(detail[0]).append(" ").append(detail[1]).append("\n");
            }
        }

        auditDetailsArea.setText(auditInfo.toString());
    }

    private JTextArea auditDetailsArea;
}