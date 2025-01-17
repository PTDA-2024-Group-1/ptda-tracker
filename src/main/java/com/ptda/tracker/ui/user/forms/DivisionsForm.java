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
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DivisionsForm extends JPanel {

    private final MainFrame mainFrame;
    private final Expense expense;
    private final List<ExpenseDivision> existingDivisions;
    private List<ExpenseDivision> divisions;
    private final String returnScreen;
    private final Runnable onSuccess;
    private final List<User> participants;
    private final ExpenseDivisionService expenseDivisionService;
    private JTable divisionsTable;
    private JLabel remainingAmountLabel, remainingPaidLabel;
    private JButton backButton, submitButton, allocateAmountButton, allocatePaidButton;
    private double remainingAmount, remainingPaid;

    private boolean isModified = false;

    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            BACK = localeManager.getTranslation("back"),
            SUBMIT = localeManager.getTranslation("submit"),
            PARTICIPANT = localeManager.getTranslation("participant"),
            AMOUNT = localeManager.getTranslation("amount"),
            PAID_AMOUNT = localeManager.getTranslation("paid.amount"),
            PAID_ALL = localeManager.getTranslation("paid_all"),
            REMAINING = localeManager.getTranslation("remaining"),
            REMAINING_PAID = localeManager.getTranslation("remaining_paid"),
            DIVISION_FORM = localeManager.getTranslation("division_form"),
            EQUAL = localeManager.getTranslation("equal"),
            CUSTOM = localeManager.getTranslation("custom"),
            TOTAL_AMOUNT = localeManager.getTranslation("total_amount"),
            EXPENSE_RESPONSIBILITY = localeManager.getTranslation("expense_responsibility"),
            CANCEL = localeManager.getTranslation("cancel"),
            AMOUNT_CANNOT_BE_NEGATIVE = localeManager.getTranslation("amount_cannot_be_negative"),
            AMOUNTS_CANNOT_BE_GREATER_THAN_SPARE_AMOUNT = localeManager.getTranslation("amounts_cannot_be_greater_than_spare_amount"),
            CANNOT_ALLOCATE = localeManager.getTranslation("cannot_allocate"),
            NO_ROW_SELECTED = localeManager.getTranslation("no_row_selected"),
            ALLOCATE_REMAINING_AMOUNT = localeManager.getTranslation("allocate_remaining_amount"),
            ALLOCATE_REMAINING_PAID = localeManager.getTranslation("allocate_remaining_paid"),
            EXIT_WITHOUT_SAVING = localeManager.getTranslation("exit_without_saving"),
            CHANGES_LOST_WARNING = localeManager.getTranslation("changes_lost_warning"),
            ERROR = localeManager.getTranslation("error");

    public DivisionsForm(MainFrame mainFrame, Expense expense, String returnScreen, Runnable onSuccess) {
        this.mainFrame = mainFrame;
        this.expense = expense;
        participants = mainFrame.getContext().getBean(BudgetAccessService.class)
                .getAllByBudgetId(expense.getBudget().getId()).stream()
                .map(BudgetAccess::getUser)
                .toList();
        this.expenseDivisionService = mainFrame.getContext().getBean(ExpenseDivisionService.class);
        existingDivisions = expenseDivisionService.getAllByExpenseId(expense.getId());

        initializeDivisions();

        this.returnScreen = returnScreen;
        this.onSuccess = onSuccess;

        initComponents();
        setListeners();
        calculateRemainingAmounts();
    }

    private void initializeDivisions() {
        divisions = new ArrayList<>();
        boolean paidAllSet = false; // Flag to ensure only one division gets Paid All

        for (User participant : participants) {
            Optional<ExpenseDivision> existingDivision = existingDivisions.stream()
                    .filter(division -> division.getUser().getId().equals(participant.getId()))
                    .findFirst();

            ExpenseDivision division = existingDivision.orElseGet(() -> {
                ExpenseDivision newDivision = new ExpenseDivision();
                newDivision.setUser(participant);
                newDivision.setExpense(expense);
                newDivision.setEqualDivision(true); // Default to equal division for new entries
                newDivision.setPaidAmount(0);
                return newDivision;
            });

            if (existingDivision.isPresent()) {
                // Ensure the existing division retains its values
                division.setAmount(existingDivision.get().getAmount());
                division.setPaidAmount(existingDivision.get().getPaidAmount());
                division.setEqualDivision(existingDivision.get().isEqualDivision());
                division.setPaidAll(existingDivision.get().isPaidAll());
            } else {
                // For new divisions, set default values
                division.setAmount(0); // Do not calculate by default

                // Assign Paid All to the creator if not already set
                if (!paidAllSet && participant.getId().equals(expense.getCreatedBy().getId())) {
                    division.setPaidAll(true);
                    division.setPaidAmount(expense.getAmount());
                    paidAllSet = true;
                }
            }

            divisions.add(division);
        }

        // If no division was assigned Paid All, default to the first participant
        if (!paidAllSet && !divisions.isEmpty()) {
            ExpenseDivision firstDivision = divisions.get(0);
            firstDivision.setPaidAll(true);
            firstDivision.setPaidAmount(expense.getAmount());
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        DivisionsTableModel tableModel = new DivisionsTableModel();
        divisionsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(divisionsTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new BorderLayout());
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        backButton = new JButton(BACK);
        submitButton = new JButton(SUBMIT);
        remainingAmountLabel = new JLabel(REMAINING + ": " + remainingAmount);
        remainingPaidLabel = new JLabel(REMAINING_PAID + ": " + remainingPaid);
        allocateAmountButton = new JButton(ALLOCATE_REMAINING_AMOUNT);
        allocatePaidButton = new JButton(ALLOCATE_REMAINING_PAID);

        allocateAmountButton.addActionListener(e -> allocateRemainingAmount());
        allocatePaidButton.addActionListener(e -> allocateRemainingPaid());
        submitButton.addActionListener(e -> submitChanges());

        buttonsPanel.add(backButton);
        buttonsPanel.add(submitButton);

        footerPanel.add(remainingAmountLabel, BorderLayout.WEST);
        footerPanel.add(remainingPaidLabel, BorderLayout.EAST);
        footerPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private void setListeners() {
        backButton.addActionListener(e -> {
            if (isModified) {
                int result = JOptionPane.showConfirmDialog(
                        this,
                        CHANGES_LOST_WARNING,
                        EXIT_WITHOUT_SAVING,
                        JOptionPane.YES_NO_OPTION
                );
                if (result == JOptionPane.YES_OPTION) {
                    resetChanges();
                    mainFrame.showScreen(returnScreen);
                }
            } else {
                mainFrame.showScreen(returnScreen);
            }
        });
        divisionsTable.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()) {
            {
                getComponent().addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        ((JTextField) getComponent()).selectAll();
                    }
                });
            }
        });
    }

    private void resetChanges() {
        initializeDivisions();
        isModified = false;
        calculateRemainingAmounts();
        divisionsTable.repaint();
    }

    private void submitChanges() {
        for (ExpenseDivision division : divisions) {
            if (division.getId() == null) {
                if (!(division.isEqualDivision() && division.getPaidAmount() == 0)) {
                    expenseDivisionService.create(division);
                }
            } else {
                if (division.isEqualDivision() && division.getPaidAmount() == 0) {
                    expenseDivisionService.deleteById(division.getId());
                } else {
                    expenseDivisionService.update(division);
                }
            }
        }

        isModified = false;
        JOptionPane.showMessageDialog(this, SUBMIT + " successful", SUBMIT, JOptionPane.INFORMATION_MESSAGE);
        onSuccess.run();
        mainFrame.showScreen(returnScreen);
    }

    private void calculateRemainingAmounts() {
        double totalEqualAmount = divisions.stream()
                .filter(ExpenseDivision::isEqualDivision)
                .mapToDouble(ExpenseDivision::getAmount)
                .sum();
        double totalPaidAmount = divisions.stream().mapToDouble(ExpenseDivision::getPaidAmount).sum();

        remainingAmount = expense.getAmount() - totalEqualAmount;
        remainingPaid = expense.getAmount() - totalPaidAmount;

        // Recalculate amounts for equal divisions if remaining exists
        long equalDivisionCount = divisions.stream().filter(ExpenseDivision::isEqualDivision).count();
        if (remainingAmount > 0 && equalDivisionCount > 0) {
            double perEqualDivision = remainingAmount / equalDivisionCount;
            for (ExpenseDivision division : divisions) {
                if (division.isEqualDivision()) {
                    division.setAmount(perEqualDivision);
                }
            }
            remainingAmount = 0;
        }

        remainingAmountLabel.setText(REMAINING + ": " + String.format("%.2f", remainingAmount));
        remainingPaidLabel.setText(REMAINING_PAID + ": " + String.format("%.2f", remainingPaid));
        divisionsTable.repaint();
    }

    private void allocateRemainingAmount() {
        int selectedRow = divisionsTable.getSelectedRow();
        if (selectedRow >= 0) {
            ExpenseDivision division = divisions.get(selectedRow);
            if (!division.isEqualDivision() && !division.isPaidAll()) {
                division.setAmount(remainingAmount);
                isModified = true;
                calculateRemainingAmounts();
                divisionsTable.repaint();
            } else {
                JOptionPane.showMessageDialog(this, CANNOT_ALLOCATE, ERROR, JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, NO_ROW_SELECTED, ERROR, JOptionPane.ERROR_MESSAGE);
        }
    }

    private void allocateRemainingPaid() {
        int selectedRow = divisionsTable.getSelectedRow();
        if (selectedRow >= 0) {
            ExpenseDivision division = divisions.get(selectedRow);
            if (!division.isEqualDivision() && !division.isPaidAll()) {
                division.setPaidAmount(remainingPaid);
                isModified = true;
                calculateRemainingAmounts();
                divisionsTable.repaint();
            } else {
                JOptionPane.showMessageDialog(this, CANNOT_ALLOCATE, ERROR, JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, NO_ROW_SELECTED, ERROR, JOptionPane.ERROR_MESSAGE);
        }
    }

    private class DivisionsTableModel extends AbstractTableModel {
        private final String[] columns = {PARTICIPANT, EQUAL, AMOUNT, PAID_ALL, PAID_AMOUNT};

        @Override
        public int getRowCount() {
            return participants.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            ExpenseDivision division = divisions.get(rowIndex);
            switch (columnIndex) {
                case 0: return participants.get(rowIndex).getName();
                case 1: return division.isEqualDivision();
                case 2: return division.isEqualDivision() ? expense.getAmount() / participants.size() : division.getAmount();
                case 3: return division.isPaidAll();
                case 4: return division.getPaidAmount();
                default: return null;
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            ExpenseDivision division = divisions.get(rowIndex);
            if (columnIndex == 2 && division.isEqualDivision()) return false;
            if (columnIndex == 4) {
                return divisions.stream().noneMatch(ExpenseDivision::isPaidAll);
            }
            return columnIndex != 0;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            ExpenseDivision division = divisions.get(rowIndex);
            switch (columnIndex) {
                case 1:
                    division.setEqualDivision((Boolean) aValue);
                    isModified = true;
                    break;
                case 2:
                    double amount = Double.parseDouble(aValue.toString());
                    division.setAmount(amount);
                    isModified = true;
                    break;
                case 3:
                    if ((Boolean) aValue) {
                        for (ExpenseDivision div : divisions) {
                            if (div != division) {
                                div.setPaidAll(false);
                                div.setPaidAmount(0);
                                fireTableCellUpdated(divisions.indexOf(div), 3);
                                fireTableCellUpdated(divisions.indexOf(div), 4);
                            }
                        }
                        division.setPaidAll(true);
                        division.setPaidAmount(expense.getAmount());
                    } else {
                        division.setPaidAll(false);
                        division.setPaidAmount(0);
                    }
                    isModified = true;
                    break;
                case 4:
                    double paidAmount = Double.parseDouble(aValue.toString());
                    division.setPaidAmount(paidAmount);
                    isModified = true;
                    break;
            }
            calculateRemainingAmounts();
            fireTableRowsUpdated(rowIndex, rowIndex);
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 1:
                case 3: return Boolean.class;
                case 2:
                case 4: return Double.class;
                default: return String.class;
            }
        }
    }
}
