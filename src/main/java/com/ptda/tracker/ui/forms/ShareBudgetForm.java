package com.ptda.tracker.ui.forms;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.BudgetAccess;
import com.ptda.tracker.models.tracker.BudgetAccessLevel;
import com.ptda.tracker.services.tracker.BudgetAccessService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.ScreenNames;

import javax.swing.*;
import java.awt.*;

public class ShareBudgetForm extends JPanel {
    private final JTextField emailField;
    private final JComboBox<BudgetAccessLevel> accessLevelComboBox;
    private final BudgetAccessService budgetAccessService;
    private final Budget budget;

    public ShareBudgetForm(MainFrame mainFrame, Budget budget) {
        this.budget = budget;
        budgetAccessService = mainFrame.getContext().getBean(BudgetAccessService.class);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Share Budget", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, gbc);

        // Email Field
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("User Email:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField();
        add(emailField, gbc);

        // Access Level Dropdown
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Access Level:"), gbc);

        gbc.gridx = 1;
        accessLevelComboBox = new JComboBox<>(BudgetAccessLevel.values());
        add(accessLevelComboBox, gbc);

        // Buttons Panel
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 3;
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("Add Participant");
        saveButton.addActionListener(e -> addParticipant());
        buttonsPanel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.BUDGET_DETAIL_VIEW));
        buttonsPanel.add(cancelButton);

        add(buttonsPanel, gbc);
    }

    private void addParticipant() {
        String email = emailField.getText().trim();
        BudgetAccessLevel accessLevel = (BudgetAccessLevel) accessLevelComboBox.getSelectedItem();

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            budgetAccessService.create(budget.getId(), email, accessLevel);
            JOptionPane.showMessageDialog(this, "Participant added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to add participant: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
