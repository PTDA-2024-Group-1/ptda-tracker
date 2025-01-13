package com.ptda.tracker.ui.user.forms;

import com.ptda.tracker.models.tracker.BudgetAccess;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.tracker.ExpenseDivision;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.tracker.BudgetAccessService;
import com.ptda.tracker.services.tracker.ExpenseDivisionService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.LocaleManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DivisionsForm extends JPanel {
    private final MainFrame mainFrame;
    private final Expense expense;
    private List<ExpenseDivision> divisions;
    private final String returnScreen;
    private final Runnable onSuccess;
    private final List<User> participants;
    private final ExpenseDivisionService expenseDivisionService;

    public DivisionsForm(MainFrame mainFrame, Expense expense, String returnScreen, Runnable onSuccess) {
        this.mainFrame = mainFrame;
        this.expense = expense;
        participants = mainFrame.getContext().getBean(BudgetAccessService.class)
                .getAllByBudgetId(expense.getBudget().getId()).stream()
                .map(BudgetAccess::getUser)
                .toList();
        this.expenseDivisionService = mainFrame.getContext().getBean(ExpenseDivisionService.class);
        this.returnScreen = returnScreen;
        this.onSuccess = onSuccess;

        initComponents();
        initDivisions();
        populateTable();
        setListeners();
    }

    private void setListeners() {
        backButton.addActionListener(e -> {
            mainFrame.showScreen(returnScreen);
            onSuccess.run();
        });
        divisionsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = divisionsTable.rowAtPoint(evt.getPoint());
                if (row >= 0) {
                    ExpenseDivision division = divisions.get(row);
                    DivisionForm divisionForm = new DivisionForm(mainFrame, division, DivisionsForm.this::refreshTable);
                    divisionForm.setVisible(true);
                }
            }
        });
    }

    private void initDivisions() {
        // Calculate equal share among participants
        int participantCount = participants.size();
        double equalShare = expense.getAmount() / (participantCount > 0 ? participantCount : 1);
        divisions = new ArrayList<>();
        List<ExpenseDivision> existingDivisions = expenseDivisionService
                .getAllByExpenseId(expense.getId());

        // Populate divisions with existing or new data
        participants.forEach(participant -> {
            // Find existing division or create a new one
            ExpenseDivision division = existingDivisions.stream()
                    .filter(div -> div.getUser().getId().equals(participant.getId()))
                    .findFirst()
                    .orElse(ExpenseDivision.builder()
                            .user(participant)
                            .expense(expense)
                            .equalDivision(true)
                            .build());

            // Set or update amount for each division
            if (!existingDivisions.contains(division)) {
                division.setAmount(equalShare);
                if (expense.getCreatedBy().getId().equals(participant.getId()) && existingDivisions.isEmpty()) {
                    division.setPaidAmount(expense.getAmount());
                }
                divisions.add(division);
            } else {
                divisions.add(division);
            }
        });
    }

    private void populateTable() {
        DefaultTableModel model = new DefaultTableModel(new String[] {
                PARTICIPANT,
                AMOUNT,
                PAID_AMOUNT
        }, 0);

        // Add rows for each division
        divisions.forEach(div -> {
            model.addRow(new Object[]{
                    div.getUser().getName(),
                    div.getAmount(),
                    div.getPaidAmount()
            });
        });
        divisionsTable.setModel(model);
    }

    void refreshTable() {
        ((DefaultTableModel) divisionsTable.getModel()).setRowCount(0);
        populateTable();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        divisionsTable = new JTable();
        add(new JScrollPane(divisionsTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        backButton = new JButton(BACK);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JTable divisionsTable;
    private JButton backButton;
    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
        BACK = localeManager.getTranslation("back"),
        PARTICIPANT = localeManager.getTranslation("participant"),
        AMOUNT = localeManager.getTranslation("amount"),
        PAID_AMOUNT = localeManager.getTranslation("paid.amount");
}
