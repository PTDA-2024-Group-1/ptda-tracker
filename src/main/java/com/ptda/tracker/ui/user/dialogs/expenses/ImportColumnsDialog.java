package com.ptda.tracker.ui.user.dialogs.expenses;

import com.ptda.tracker.util.ImportSharedData;

import javax.swing.*;

public class ImportColumnsDialog extends JDialog {
    private final ImportSharedData sharedData;

    public ImportColumnsDialog(JFrame parent) {
        super(parent, "Import Columns", true);
        this.sharedData = ImportSharedData.getInstance();

        initComponents();
        setListeners();
    }

    private void initComponents() {
        cancelButton = new JButton("Cancel");
        confirmButton = new JButton("Confirm");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);

        add(buttonPanel);
    }

    private void setListeners() {
        cancelButton.addActionListener(e -> {
            dispose();
        });
        confirmButton.addActionListener(e -> {

        });
    }

    private JButton cancelButton, confirmButton;

}
