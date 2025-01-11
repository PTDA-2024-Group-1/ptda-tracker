package com.ptda.tracker.ui.user.forms;

import com.ptda.tracker.models.tracker.BudgetAccess;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.tracker.ExpenseDivision;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.tracker.BudgetAccessService;
import com.ptda.tracker.services.tracker.ExpenseDivisionService;
import com.ptda.tracker.ui.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DivisionsForm extends JPanel {
    private final MainFrame mainFrame;
    private final Expense expense;
    private final List<ExpenseDivision> divisions, existingDivisions;
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
        this.existingDivisions = expenseDivisionService
                .getAllByExpenseId(expense.getId());
        this.divisions = new ArrayList<>(participants.size());
        this.returnScreen = returnScreen;
        this.onSuccess = onSuccess;

        initComponents();
        initDivisions();
        populateTable();
        setListeners();
    }

    private void setListeners() {
        backButton.addActionListener(e -> mainFrame.showScreen(returnScreen));
        submitButton.addActionListener(e -> {
            submit();
            onSuccess.run();
            mainFrame.showScreen(returnScreen);
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

    private void submit() {
        divisions.forEach(division -> {
            try {
                if (division.getId() == null) {
                    expenseDivisionService.create(division);
                } else {
                    expenseDivisionService.update(division);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private void initDivisions() {
        // Calculate equal share among participants
        int participantCount = participants.size();
        double equalShare = expense.getAmount() / (participantCount > 0 ? participantCount : 1);

        // Populate divisions with existing or new data
        participants.forEach(participant -> {
            // Find existing division or create a new one
            ExpenseDivision division = existingDivisions.stream()
                    .filter(div -> div.getUser().getId().equals(participant.getId()))
                    .findFirst()
                    .orElseGet(() -> {
                        Expense expense = existingDivisions.isEmpty() ? null : existingDivisions.getFirst().getExpense();
                        return ExpenseDivision.builder()
                                .user(participant)
                                .expense(expense)
                                .build();
                    });

            // Set or update amount for each division
            if (!existingDivisions.contains(division)) {
                division.setAmount(equalShare);
                divisions.add(division);
            } else {
                divisions.add(division); // Add existing divisions as-is
            }
        });
    }

    private void populateTable() {
        DefaultTableModel model = new DefaultTableModel(new String[] {
                PARTICIPANT,
                AMOUNT,
                PERCENTAGE,
                PAID_AMOUNT
        }, 0);

        // Add rows for each division
        divisions.forEach(div -> {
            model.addRow(new Object[]{
                    div.getUser().getName(),
                    div.getAmount(), // Ensure this is correctly set
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
        submitButton = new JButton(SUBMIT);
        buttonPanel.add(backButton);
        buttonPanel.add(submitButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JTable divisionsTable;
    private JButton backButton, submitButton;
    private static final String
        DIVISIONS = "divisions",
        DIVISION = "division",
        BACK = "back",
        SUBMIT = "submit",
        PARTICIPANT = "participant",
        AMOUNT = "amount",
        PERCENTAGE = "percentage",
        PAID_AMOUNT = "paid amount",
        STATE = "state";
}
