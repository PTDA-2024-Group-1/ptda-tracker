package com.ptda.tracker.ui.user.dialogs.expenses;

import com.ptda.tracker.util.ExpensesImportSharedData;

import javax.swing.*;
import java.awt.*;

public class ImportValueTreatmentDialog extends JDialog {
    private final ExpensesImportSharedData sharedData;
    private final Runnable onDone;

    public ImportValueTreatmentDialog(JFrame parent, Runnable onDone) {
        super(parent, IMPORT_VALUE_TREATMENT, true);
        this.sharedData = ExpensesImportSharedData.getInstance();
        this.onDone = onDone;

        initComponents();
        prefillSelection();
        setListeners();
    }

    private void prefillSelection() {
        // Prefill based on shared data
        String valueTreatment = sharedData.getValueTreatment(); // Assumes sharedData stores this setting
        if (valueTreatment != null) {
            switch (valueTreatment) {
                case "NEGATIVE_AS_EXPENSE":
                    negativeAsExpenseButton.setSelected(true);
                    break;
                case "POSITIVE_AS_EXPENSE":
                    positiveAsExpenseButton.setSelected(true);
                    break;
                case "ABSOLUTE_VALUE":
                    absoluteValueButton.setSelected(true);
                    break;
                default:
                    negativeAsExpenseButton.setSelected(true); // Default to negative
            }
        } else {
            negativeAsExpenseButton.setSelected(true); // Default to negative
        }
    }

    private void setListeners() {
        skipButton.addActionListener(e -> dispose());

        confirmButton.addActionListener(e -> {
            String selectedOption;
            if (negativeAsExpenseButton.isSelected()) {
                selectedOption = "NEGATIVE_AS_EXPENSE";
            } else if (positiveAsExpenseButton.isSelected()) {
                selectedOption = "POSITIVE_AS_EXPENSE";
            } else {
                selectedOption = "ABSOLUTE_VALUE";
            }

            sharedData.setValueTreatment(selectedOption);
            onDone.run();
            dispose();
        });
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Radio buttons for options
        negativeAsExpenseButton = new JRadioButton(IMPORT_NEGATIVE_VALUES_AS_EXPENSES);
        positiveAsExpenseButton = new JRadioButton(IMPORT_POSITIVE_VALUES_AS_EXPENSES);
        absoluteValueButton = new JRadioButton(TREAT_ALL_VALUES_AS_ABSOLUTE_EXPENSES);

        // Group the radio buttons
        ButtonGroup group = new ButtonGroup();
        group.add(negativeAsExpenseButton);
        group.add(positiveAsExpenseButton);
        group.add(absoluteValueButton);

        // Add buttons to panel
        JPanel optionsPanel = new JPanel(new GridLayout(3, 1));
        optionsPanel.setBorder(BorderFactory.createTitledBorder(HOW_SHOULD_VALUES_BE_TREATED));
        optionsPanel.add(negativeAsExpenseButton);
        optionsPanel.add(positiveAsExpenseButton);
        optionsPanel.add(absoluteValueButton);

        // Confirm and cancel buttons
        confirmButton = new JButton(CONFIRM);
        skipButton = new JButton(SKIP);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(skipButton);
        buttonPanel.add(confirmButton);

        // Add panels to dialog
        add(optionsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
    }

    private JRadioButton negativeAsExpenseButton, positiveAsExpenseButton, absoluteValueButton;
    private JButton confirmButton, skipButton;
    private static final String
            IMPORT_VALUE_TREATMENT = "Import Value Treatment",
            HOW_SHOULD_VALUES_BE_TREATED = "How should values be treated?",
            IMPORT_NEGATIVE_VALUES_AS_EXPENSES = "Import negative values as expenses",
            IMPORT_POSITIVE_VALUES_AS_EXPENSES = "Import positive values as expenses",
            TREAT_ALL_VALUES_AS_ABSOLUTE_EXPENSES = "Treat all values as absolute expenses",
            SKIP = "Skip",
            CONFIRM = "Confirm";
}
