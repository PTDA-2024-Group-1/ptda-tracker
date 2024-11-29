package com.ptda.tracker.ui.forms;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.BudgetAccessLevel;
import com.ptda.tracker.services.tracker.BudgetAccessService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.views.BudgetDetailView;
import com.ptda.tracker.util.ScreenNames;

import javax.swing.*;
import java.awt.*;

public class ShareBudgetForm extends JPanel {
    private final MainFrame mainFrame;
    private final BudgetAccessService budgetAccessService;
    private final Budget budget;

    private JComboBox<BudgetAccessLevel> accessLevelComboBox;
    private JTextField emailField;
    private JButton cancelButton, saveButton;

    private static final String
            SHARE_BUDGET = "Share Budget",
            USER_EMAIL = "User Email",
            ACCESS_LEVEL = "Access Level",
            CANCEL = "Cancel",
            ADD_PARTICIPANT = "Add Participant",
            EMAIL_REQUIRED = "Email is required.",
            ERROR = "Error",
            PARTICIPANT_ADDED_SUCCESSFULLY = "Participant added successfully!",
            SUCCESS = "Success",
            FAILED_TO_ADD_PARTICIPANT = "Failed to add participant: ";

    public ShareBudgetForm(MainFrame mainFrame, Budget budget) {
        this.mainFrame = mainFrame;
        this.budget = budget;
        budgetAccessService = mainFrame.getContext().getBean(BudgetAccessService.class);

        initUI();
        setListeners();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel(SHARE_BUDGET, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, gbc);

        // Email Field
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel emailLabel = new JLabel(USER_EMAIL + ":");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(emailLabel, gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        add(emailField, gbc);

        // Access Level Dropdown
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel accessLevelLabel = new JLabel(ACCESS_LEVEL + ":");
        accessLevelLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(accessLevelLabel, gbc);

        gbc.gridx = 1;
        accessLevelComboBox = new JComboBox<>(BudgetAccessLevel.values());
        add(accessLevelComboBox, gbc);

        // Buttons Panel
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 3;
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        cancelButton = new JButton(CANCEL);
        buttonsPanel.add(cancelButton);

        saveButton = new JButton(ADD_PARTICIPANT);
        buttonsPanel.add(saveButton);

        add(buttonsPanel, gbc);
    }

    private void setListeners() {
        cancelButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.BUDGET_DETAIL_VIEW));
        saveButton.addActionListener(e -> addParticipant());
    }

    private void addParticipant() {
        String email = emailField.getText().trim();
        BudgetAccessLevel accessLevel = (BudgetAccessLevel) accessLevelComboBox.getSelectedItem();

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, EMAIL_REQUIRED, ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            budgetAccessService.create(budget.getId(), email, accessLevel);
            JOptionPane.showMessageDialog(this, PARTICIPANT_ADDED_SUCCESSFULLY, SUCCESS, JOptionPane.INFORMATION_MESSAGE);
            mainFrame.registerAndShowScreen(ScreenNames.BUDGET_DETAIL_VIEW, new BudgetDetailView(mainFrame, budget));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, FAILED_TO_ADD_PARTICIPANT + e.getMessage(), ERROR, JOptionPane.ERROR_MESSAGE);
        }
    }
}