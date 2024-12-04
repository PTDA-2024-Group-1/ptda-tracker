package com.ptda.tracker.ui.user.views;

import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.services.tracker.ExpenseAuditService;
import com.ptda.tracker.services.user.UserService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.ScreenNames;


import javax.swing.*;
import java.awt.*;
import java.util.List;


public class ExpenseAuditDetailView extends JPanel {
    private final MainFrame mainFrame;
    private final Expense expense;
    private final ExpenseAuditService expenseAuditService;
    private JTextArea auditDetailsArea;

    public ExpenseAuditDetailView(MainFrame mainFrame, Expense expense) {
        this.mainFrame = mainFrame;
        this.expense = expense;
        this.expenseAuditService = mainFrame.getContext().getBean(ExpenseAuditService.class);

        initComponents();
        populateAuditDetails();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        auditDetailsArea = new JTextArea();
        auditDetailsArea.setEditable(false);
        add(new JScrollPane(auditDetailsArea), BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.EXPENSE_DETAIL_VIEW));
        add(backButton, BorderLayout.SOUTH);
    }

    private void populateAuditDetails() {
        List<Object[]> auditDetails = expenseAuditService.getExpenseRevisionsWithDetails(expense.getId());
        StringBuilder auditInfo = new StringBuilder();
        for (Object[] detail : auditDetails) {
            auditInfo.append(detail[0]).append(" ").append(detail[1]).append("\n");
        }
        auditDetailsArea.setText(auditInfo.toString());
    }
}