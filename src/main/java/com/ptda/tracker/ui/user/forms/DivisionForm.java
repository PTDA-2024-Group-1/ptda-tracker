package com.ptda.tracker.ui.user.forms;

import com.ptda.tracker.models.tracker.ExpenseDivision;
import com.ptda.tracker.services.tracker.ExpenseDivisionService;
import com.ptda.tracker.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
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
            JOptionPane.showMessageDialog(this, "Amounts cannot be negative.");
            return;
        }
        division.setAmount(amount);
        division.setPaidAmount(paidAmount);

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
    private static final String
            DIVISION_FORM = "Division Form",
            SUBMIT = "Submit",
            CANCEL = "Cancel",
            EQUAL = "Equal",
            CUSTOM = "Custom",
            TOTAL_AMOUNT = "Total Amount",
            PAID_AMOUNT = "Paid Amount",
            EXPENSE_RESPONSIBILITY = "Expense Responsibility";
}
