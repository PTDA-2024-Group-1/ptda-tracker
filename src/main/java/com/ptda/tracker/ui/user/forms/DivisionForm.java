package com.ptda.tracker.ui.user.forms;

import com.ptda.tracker.models.tracker.ExpenseDivision;
import com.ptda.tracker.services.tracker.ExpenseDivisionService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.LocaleManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class DivisionForm extends JDialog {
    private final MainFrame mainFrame;
    private final ExpenseDivisionService expenseDivisionService;
    private ExpenseDivision division;
    private final Runnable onSuccess;

    public DivisionForm(MainFrame mainFrame, ExpenseDivision division, Runnable onSuccess) {
        super(mainFrame);
        setTitle(DIVISION_FORM);
        setModal(true);
        this.mainFrame = mainFrame;
        this.expenseDivisionService = mainFrame.getContext().getBean(ExpenseDivisionService.class);
        this.division = division;
        this.onSuccess = onSuccess;

        initComponents();
        setListeners();
        setValues(division);
    }

    private void setListeners() {
        submitButton.addActionListener(e -> submit());
        cancelButton.addActionListener(e -> dispose());
        amountComboBox.addActionListener(e -> {
            amountTextField.setVisible(!Objects.equals(amountComboBox.getSelectedItem(), EQUAL));
        });
        paidAmountComboBox.addActionListener(e -> {
            paidAmountTextField.setVisible(!Objects.equals(paidAmountComboBox.getSelectedItem(), TOTAL_AMOUNT));
        });
    }

    private void submit() {
        if (division == null) {
            division = new ExpenseDivision();
        }

        double amount = Double.parseDouble(amountTextField.getText());
        double paidAmount = Double.parseDouble(paidAmountTextField.getText());
        if (amount < 0 || paidAmount < 0) {
            JOptionPane.showMessageDialog(this, AMOUNT_CANNOT_BE_NEGATIVE);
            return;
        }
        // can't submit if amount or paid amount is greater than spare amount
        List<ExpenseDivision> expenseDivisions = expenseDivisionService.getAllByExpenseId(division.getExpense().getId());
        double spareAmount = division.getExpense().getAmount()
                - expenseDivisions.stream().mapToDouble(ExpenseDivision::getAmount).sum() + division.getAmount();;
        double sparePaidAmount = division.getExpense().getAmount()
                - expenseDivisions.stream().mapToDouble(ExpenseDivision::getPaidAmount).sum() + division.getPaidAmount();
        if (amount > spareAmount || paidAmount > sparePaidAmount) {
            JOptionPane.showMessageDialog(this, AMOUNTS_CANNOT_BE_GREATER_THAN_SPARE_AMOUNT);
            return;
        }

        if (amountComboBox.getSelectedItem() == EQUAL) {
            division.setEqualDivision(true);
        } else {
            division.setEqualDivision(false);
            division.setAmount(amount);
        }
        if (paidAmountComboBox.getSelectedItem() == TOTAL_AMOUNT) {
            division.setPaidAll(true);
        } else {
            division.setPaidAll(false);
            division.setPaidAmount(paidAmount);
        }

        try {
            if (division.getId() == null) {
                expenseDivisionService.create(division);
            } else {
                expenseDivisionService.update(division);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (onSuccess != null) onSuccess.run();
        dispose();
    }

    private void setValues(ExpenseDivision division) {
        if (division == null) return;
        amountComboBox.setSelectedItem(division.isEqualDivision() ? EQUAL : CUSTOM);
        paidAmountComboBox.setSelectedItem(division.isPaidAll() ? TOTAL_AMOUNT : CUSTOM);
//        amountTextField.setVisible(!division.isEqualDivision());
        amountTextField.setText(String.valueOf(division.getAmount()));
//        paidAmountTextField.setVisible(!division.isPaidAll());
        paidAmountTextField.setText(String.valueOf(division.getPaidAmount()));
    }

    private void initComponents() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Expense Responsibility
        JPanel responsibilityPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        responsibilityPanel.add(new JLabel(EXPENSE_RESPONSIBILITY + ":"));
        amountComboBox = new JComboBox<>(new String[]{EQUAL, CUSTOM});
        responsibilityPanel.add(amountComboBox);
        amountTextField = new JTextField();
        responsibilityPanel.add(amountTextField);
        contentPanel.add(responsibilityPanel);

        // Paid Amount
        JPanel paidAmountPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        paidAmountPanel.add(new JLabel(PAID_AMOUNT + ":"));
        paidAmountComboBox = new JComboBox<>(new String[]{CUSTOM, TOTAL_AMOUNT});
        paidAmountPanel.add(paidAmountComboBox);
        paidAmountTextField = new JTextField();
        paidAmountPanel.add(paidAmountTextField);
        contentPanel.add(paidAmountPanel);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        submitButton = new JButton(SUBMIT);
        cancelButton = new JButton(CANCEL);
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        contentPanel.add(buttonPanel);

        add(contentPanel);
        pack();
        setLocationRelativeTo(mainFrame);
    }

    private JComboBox<String> amountComboBox, paidAmountComboBox;
    private JTextField amountTextField, paidAmountTextField;
    private JButton submitButton, cancelButton;
    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
        DIVISION_FORM = localeManager.getTranslation("division_form"),
        EQUAL = localeManager.getTranslation("equal"),
        CUSTOM = localeManager.getTranslation("custom"),
        TOTAL_AMOUNT = localeManager.getTranslation("total_amount"),
        EXPENSE_RESPONSIBILITY = localeManager.getTranslation("expense_responsibility"),
        PAID_AMOUNT = localeManager.getTranslation("paid.amount"),
        SUBMIT = localeManager.getTranslation("submit"),
        CANCEL = localeManager.getTranslation("cancel"),
        AMOUNT_CANNOT_BE_NEGATIVE = localeManager.getTranslation("amount_cannot_be_negative"),
        AMOUNTS_CANNOT_BE_GREATER_THAN_SPARE_AMOUNT = localeManager.getTranslation("amounts_cannot_be_greater_than_spare_amount");
}
