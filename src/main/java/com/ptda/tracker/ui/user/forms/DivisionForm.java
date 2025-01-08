package com.ptda.tracker.ui.user.forms;

import com.ptda.tracker.models.tracker.ExpenseDivision;
import com.ptda.tracker.services.tracker.ExpenseDivisionService;
import com.ptda.tracker.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class DivisionForm extends JDialog {
    private final MainFrame mainFrame;
    private ExpenseDivision division;
    private final Runnable onSuccess;

    public DivisionForm(MainFrame mainFrame, ExpenseDivision division, Runnable onSuccess) {
        super(mainFrame);
        setTitle(DIVISION_FORM);
        setModal(true);
        this.mainFrame = mainFrame;
        this.division = division;
        this.onSuccess = onSuccess;

        initComponents();
        setListeners();
    }

    private void setListeners() {
        confirmButton.addActionListener(e -> {
            if (division == null) {
                division = new ExpenseDivision();
            }

            if (Objects.equals(responsibilityTypeComboBox.getSelectedItem(), PERCENTAGE)) {
                division.setPercentage(Double.parseDouble(responsibilityValueTextField.getText()));
            } else {
                division.setAmount(Double.parseDouble(responsibilityValueTextField.getText()));
            }
            division.setPaidAmount(Double.parseDouble(paidAmountValueTextField.getText()));

            try {
                if (division.getId() == null) {
                    mainFrame.getContext().getBean(ExpenseDivisionService.class).create(division);
                } else {
                    mainFrame.getContext().getBean(ExpenseDivisionService.class).update(division);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (onSuccess != null) onSuccess.run();
            dispose();
        });
        cancelButton.addActionListener(e -> dispose());
    }

    private void initComponents() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Expense Responsibility
        JPanel responsibilityPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        responsibilityPanel.add(new JLabel(EXPENSE_RESPONSIBILITY + ":"));
        responsibilityTypeComboBox = new JComboBox<>(new String[]{ABSOLUTE, PERCENTAGE});
        responsibilityPanel.add(responsibilityTypeComboBox);
        responsibilityValueTextField = new JTextField();
        responsibilityPanel.add(responsibilityValueTextField);
        contentPanel.add(responsibilityPanel);

        // Paid Amount
        JPanel paidAmountPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        paidAmountPanel.add(new JLabel(PAID_AMOUNT + ":"));
        paidAmountTypeComboBox = new JComboBox<>(new String[]{ABSOLUTE, PERCENTAGE});
        paidAmountPanel.add(paidAmountTypeComboBox);
        paidAmountValueTextField = new JTextField();
        paidAmountPanel.add(paidAmountValueTextField);
        contentPanel.add(paidAmountPanel);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        confirmButton = new JButton(CONFIRM);
        cancelButton = new JButton(CANCEL);
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        contentPanel.add(buttonPanel);

        add(contentPanel);
        pack();
        setLocationRelativeTo(mainFrame);
    }

    private JComboBox<String> responsibilityTypeComboBox, paidAmountTypeComboBox;
    private JTextField responsibilityValueTextField, paidAmountValueTextField;
    private JButton confirmButton, cancelButton;
    private static final String
            DIVISION_FORM = "Division Form",
            CONFIRM = "Confirm",
            CANCEL = "Cancel",
            ABSOLUTE = "Absolute",
            PERCENTAGE = "Percentage",
            PAID_AMOUNT = "Paid Amount",
            EXPENSE_RESPONSIBILITY = "Expense Responsibility";
}
