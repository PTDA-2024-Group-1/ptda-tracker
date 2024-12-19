package com.ptda.tracker.ui.user.dialogs.expenses;

import com.ptda.tracker.util.ImportSharedData;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Map;

public class ImportDateFormatDialog extends JDialog {
    private final ImportSharedData sharedData;

    private JComboBox<String> dateFormatComboBox;
    private JTextField customDateFormatField;
    private JButton confirmButton, cancelButton;
    private Runnable onDone;

    public ImportDateFormatDialog(JFrame parent, Runnable onDone) {
        super(parent, SET_DATE_FORMAT, true);
        this.sharedData = ImportSharedData.getInstance();
        this.onDone = onDone;

        initComponents();
        prefillDateFormat();
        setListeners();
    }

    private void prefillDateFormat() {
        String storedDateFormat = sharedData.getDateFormat();

        if (storedDateFormat == null || storedDateFormat.isEmpty()) {
            // Default selection if no format is stored
            dateFormatComboBox.setSelectedIndex(0);
        } else {
            boolean foundInPredefined = false;

            for (int i = 0; i < dateFormatComboBox.getItemCount(); i++) {
                if (storedDateFormat.equals(dateFormatComboBox.getItemAt(i))) {
                    dateFormatComboBox.setSelectedIndex(i);
                    foundInPredefined = true;
                    break;
                }
            }

            if (!foundInPredefined) {
                // Select "Other" and prefill custom format field
                dateFormatComboBox.setSelectedItem(OTHER);
                customDateFormatField.setText(storedDateFormat);
                customDateFormatField.setEnabled(true);
            }
        }
    }

    private void setListeners() {
        cancelButton.addActionListener(e -> dispose());

        confirmButton.addActionListener(e -> {
            String selectedDateFormat = getSelectedDateFormat();

            if (isValidDateFormat(selectedDateFormat)) {
                sharedData.setDateFormat(selectedDateFormat);
                onDone.run();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid date format. Please correct your input.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private String getSelectedDateFormat() {
        if (OTHER.equals(dateFormatComboBox.getSelectedItem())) {
            return customDateFormatField.getText();
        }
        return (String) dateFormatComboBox.getSelectedItem();
    }

    private boolean isValidDateFormat(String format) {
        try {
            new SimpleDateFormat(format);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Date Format Selection Panel
        JPanel datePanel = new JPanel(new FlowLayout());
        datePanel.setBorder(BorderFactory.createTitledBorder(DATE_FORMAT));

        dateFormatComboBox = new JComboBox<>(new String[]{
                "dd/MM/yyyy", "dd-MM-yyyy", "MM/dd/yyyy", "MM-dd-yyyy", "yyyy/MM/dd", "yyyy-MM-dd", OTHER
        });

        customDateFormatField = new JTextField(10);
        customDateFormatField.setEnabled(false);

        dateFormatComboBox.addActionListener(e -> {
            boolean isOther = OTHER.equals(dateFormatComboBox.getSelectedItem());
            customDateFormatField.setEnabled(isOther);
        });

        datePanel.add(new JLabel(SELECT_DATE_FORMAT + ":"));
        datePanel.add(dateFormatComboBox);
        datePanel.add(new JLabel(CUSTOM + ":"));
        datePanel.add(customDateFormatField);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        confirmButton = new JButton(CONFIRM);
        cancelButton = new JButton(CANCEL);

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        // Add components to the dialog
        add(datePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
    }

    private static final String
            SET_DATE_FORMAT = "Set Date Format",
            SELECT_DATE_FORMAT = "Select Date Format",
            CUSTOM = "Custom",
            OTHER = "Other",
            DATE_FORMAT = "Date Format",
            CONFIRM = "Confirm",
            CANCEL = "Cancel";
}
