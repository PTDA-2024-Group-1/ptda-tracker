package com.ptda.tracker.ui.user.dialogs;

import com.ptda.tracker.services.assistance.TicketService;
import com.ptda.tracker.services.tracker.BudgetService;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SummaryDialog extends JDialog {
    private final MainFrame mainFrame;
    int budgetCount, expenseCountBudget, expenseCountPersonal, pendingTicketCount;
    private final Long userId = UserSession.getInstance().getUser().getId();

    public SummaryDialog(MainFrame mainFrame) {
        super(mainFrame, SUMMARY, true);
        this.mainFrame = mainFrame;

        initComponents();
        loadData(mainFrame);

        pack();
        setLocationRelativeTo(null);
    }

    private void loadData(MainFrame mainFrame) {
        budgetCount = mainFrame.getContext().getBean(BudgetService.class).getCountByUserId(userId);
        expenseCountPersonal = mainFrame.getContext().getBean(ExpenseService.class).getCountByUserIdPersonal(userId);
        expenseCountBudget = mainFrame.getContext().getBean(ExpenseService.class).getCountByUserId(userId) - expenseCountPersonal;
        pendingTicketCount = mainFrame.getContext().getBean(TicketService.class).getCountByUserIdAndStatus(userId, false);

        budgetCountLabel.setText(BUDGETS + ": " + budgetCount);
        budgetExpensesCountLabel.setText(BUDGET_EXPENSES + ": " + expenseCountBudget);
        personalExpensesCountLabel.setText(PERSONAL_EXPENSES + ": " + expenseCountPersonal);
        ticketCountLabel.setText(PENDING_TICKETS + ": " + pendingTicketCount);
    }

    private void initComponents() {
        setLayout(new GridLayout(6, 1));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        budgetCountLabel = new JLabel();
        budgetExpensesCountLabel = new JLabel();
        personalExpensesCountLabel = new JLabel();
        ticketCountLabel = new JLabel();

        add(budgetCountLabel);
        add(budgetExpensesCountLabel);
        add(personalExpensesCountLabel);
        add(ticketCountLabel);
        add(new JLabel());

        closeButton = new JButton(CLOSE);
        closeButton.addActionListener(e -> dispose());
        add(closeButton);
    }

    private JLabel budgetCountLabel, budgetExpensesCountLabel, personalExpensesCountLabel, ticketCountLabel;
    private JButton closeButton;
    private static final String
            BUDGETS = "Budgets",
            BUDGET_EXPENSES = "Expenses Created in Budgets",
            PERSONAL_EXPENSES = "Personal Expenses",
            PENDING_TICKETS = "Pending Tickets",
            SUMMARY = "Summary",
            CLOSE = "Close";
}
