package com.ptda.tracker.ui.user.dialogs.expenses;

import com.ptda.tracker.util.ExpensesImportSharedData;
import com.ptda.tracker.util.LocaleManager;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class ImportDateFormatDialog extends JDialog {
    private final ExpensesImportSharedData sharedData;
    private final Runnable onDone;

    public ImportDateFormatDialog(JFrame parent, Runnable onDone) {
        super(parent, SET_DATE_FORMAT, true);
        this.sharedData = ExpensesImportSharedData.getInstance();
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
                customDateFormatPanel.setVisible(true);
            }
        }
    }

    private void setListeners() {
        dateFormatComboBox.addActionListener(e -> {
            boolean isOther = OTHER.equals(dateFormatComboBox.getSelectedItem());
            customDateFormatPanel.setVisible(isOther);
            pack();
        });

        cancelButton.addActionListener(e -> dispose());

        confirmButton.addActionListener(e -> {
            String selectedDateFormat = getSelectedDateFormat();

            if (isValidDateFormat(selectedDateFormat)) {
                sharedData.setDateFormat(selectedDateFormat);
                onDone.run();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        INVALID_DATE_FORMAT,
                        ERROR,
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

        datePanel.add(new JLabel(SELECT_DATE_FORMAT + ":"));
        datePanel.add(dateFormatComboBox);

        customDateFormatPanel = new JPanel(new FlowLayout());
        customDateFormatPanel.add(new JLabel(CUSTOM + ":"));
        customDateFormatField = new JTextField(10);
        customDateFormatPanel.add(customDateFormatField);
        customDateFormatPanel.setVisible(false);
        pack();
        datePanel.add(customDateFormatPanel);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        confirmButton = new JButton(CONFIRM);
        cancelButton = new JButton(CANCEL);

        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);

        // Add components to the dialog
        add(datePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
    }

    private JComboBox<String> dateFormatComboBox;
    private JPanel customDateFormatPanel;
    private JTextField customDateFormatField;
    private JButton confirmButton, cancelButton;
    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            SET_DATE_FORMAT = localeManager.getTranslation("set.date.format"),
            SELECT_DATE_FORMAT = localeManager.getTranslation("select.date.format"),
            CUSTOM = localeManager.getTranslation("custom"),
            OTHER = localeManager.getTranslation("other"),
            DATE_FORMAT = localeManager.getTranslation("date.format"),
            CONFIRM = localeManager.getTranslation("confirm"),
            CANCEL = localeManager.getTranslation("cancel"),
            INVALID_DATE_FORMAT = localeManager.getTranslation("invalid.date.format"),
            ERROR = localeManager.getTranslation("error");
}
