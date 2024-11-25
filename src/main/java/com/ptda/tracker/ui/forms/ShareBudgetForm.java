package com.ptda.tracker.ui.forms;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.BudgetAccessLevel;
import com.ptda.tracker.services.tracker.BudgetAccessService;
import com.ptda.tracker.ui.MainFrame;
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
        JLabel titleLabel = new JLabel("Share Budget", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(56, 56, 56)); // Cor do título
        add(titleLabel, gbc);

        // Email Field
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel emailLabel = new JLabel("User Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(emailLabel, gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        styleTextField(emailField);
        add(emailField, gbc);

        // Access Level Dropdown
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel accessLevelLabel = new JLabel("Access Level:");
        accessLevelLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(accessLevelLabel, gbc);

        gbc.gridx = 1;
        accessLevelComboBox = new JComboBox<>(BudgetAccessLevel.values());
        styleComboBox(accessLevelComboBox);
        add(accessLevelComboBox, gbc);

        // Buttons Panel
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 3;
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        cancelButton = new JButton("Cancel");
        styleButton(cancelButton);
        buttonsPanel.add(cancelButton);

        saveButton = new JButton("Add Participant");
        styleButton(saveButton);
        buttonsPanel.add(saveButton);

        add(buttonsPanel, gbc);

        setBackground(new Color(240, 240, 240)); // Cor de fundo suave
    }

    private void setListeners() {
        cancelButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.BUDGET_DETAIL_VIEW));
        saveButton.addActionListener(e -> addParticipant());
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBackground(new Color(255, 255, 255)); // Fundo branco
        field.setForeground(new Color(56, 56, 56)); // Texto escuro
        field.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2)); // Borda suave
    }

    private void styleComboBox(JComboBox<BudgetAccessLevel> comboBox) {
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        comboBox.setBackground(new Color(255, 255, 255)); // Fundo branco
        comboBox.setForeground(new Color(56, 56, 56)); // Texto escuro
        comboBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2)); // Borda suave
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(56, 56, 56)); // Fundo do botão
        button.setForeground(Color.WHITE); // Texto branco
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 40));
        button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Efeito hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 0, 0)); // Fundo ao passar o mouse
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(56, 56, 56)); // Fundo padrão
            }
        });
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
