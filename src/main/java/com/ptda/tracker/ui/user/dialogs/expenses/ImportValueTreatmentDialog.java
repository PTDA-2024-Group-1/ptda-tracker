package com.ptda.tracker.ui.user.dialogs.expenses;

import com.ptda.tracker.util.ImportSharedData;

import javax.swing.*;
import java.awt.*;

public class ImportValueTreatmentDialog extends JDialog {
    private final ImportSharedData sharedData;
    private JRadioButton negativeAsExpenseButton, positiveAsExpenseButton, absoluteValueButton;
    private JButton confirmButton, cancelButton;

    public ImportValueTreatmentDialog(JFrame parent) {
        super(parent, "Import Value Treatment", true);
        this.sharedData = ImportSharedData.getInstance();

        initComponents();
        prefillSelection();
        setListeners();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Radio buttons for options
        negativeAsExpenseButton = new JRadioButton("Import negative values as expenses");
        positiveAsExpenseButton = new JRadioButton("Import positive values as expenses");
        absoluteValueButton = new JRadioButton("Treat all values as absolute expenses");

        // Group the radio buttons
        ButtonGroup group = new ButtonGroup();
        group.add(negativeAsExpenseButton);
        group.add(positiveAsExpenseButton);
        group.add(absoluteValueButton);

        // Add buttons to panel
        JPanel optionsPanel = new JPanel(new GridLayout(3, 1));
        optionsPanel.setBorder(BorderFactory.createTitledBorder("How should values be treated?"));
        optionsPanel.add(negativeAsExpenseButton);
        optionsPanel.add(positiveAsExpenseButton);
        optionsPanel.add(absoluteValueButton);

        // Confirm and cancel buttons
        confirmButton = new JButton("Confirm");
        cancelButton = new JButton("Cancel");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);

        // Add panels to dialog
        add(optionsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null); // Center the dialog
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
        cancelButton.addActionListener(e -> dispose());

        confirmButton.addActionListener(e -> {
            String selectedOption;
            if (negativeAsExpenseButton.isSelected()) {
                selectedOption = "NEGATIVE_AS_EXPENSE";
            } else if (positiveAsExpenseButton.isSelected()) {
                selectedOption = "POSITIVE_AS_EXPENSE";
            } else {
                selectedOption = "ABSOLUTE_VALUE";
            }

            // Save the selection to shared data
            sharedData.setValueTreatment(selectedOption);

            dispose();
        });
    }
}
