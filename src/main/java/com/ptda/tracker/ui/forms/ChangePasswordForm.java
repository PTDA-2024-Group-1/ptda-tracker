package com.ptda.tracker.ui.forms;

import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.user.UserService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class ChangePasswordForm extends JPanel {
    private final UserService userService;
    private final JPasswordField currentPasswordField;
    private final JPasswordField newPasswordField;
    private final JPasswordField confirmPasswordField;
    private final JButton saveButton;
    private final JButton cancelButton;

    public ChangePasswordForm(MainFrame mainFrame) {
        userService = mainFrame.getContext().getBean(UserService.class);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel title = new JLabel("Change Password", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(56, 56, 56)); // Cor escura para o título
        add(title, gbc);

        // Current Password
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel currentPasswordLabel = new JLabel("Current Password:");
        currentPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(currentPasswordLabel, gbc);

        gbc.gridx = 1;
        currentPasswordField = new JPasswordField(20);
        stylePasswordField(currentPasswordField);
        add(currentPasswordField, gbc);

        // New Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel newPasswordLabel = new JLabel("New Password:");
        newPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(newPasswordLabel, gbc);

        gbc.gridx = 1;
        newPasswordField = new JPasswordField(20);
        stylePasswordField(newPasswordField);
        add(newPasswordField, gbc);

        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(confirmPasswordLabel, gbc);

        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        stylePasswordField(confirmPasswordField);
        add(confirmPasswordField, gbc);

        // Cancel Button
        gbc.gridx = 0;
        gbc.gridy = 4;
        cancelButton = new JButton("Cancel");
        styleButton(cancelButton);
        cancelButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN));
        add(cancelButton, gbc);

        // Save Button
        gbc.gridx = 1;
        saveButton = new JButton("Save");
        styleButton(saveButton);
        saveButton.addActionListener(e -> onSave(mainFrame));
        add(saveButton, gbc);

        setBackground(new Color(240, 240, 240)); // Cor de fundo suave
    }

    private void stylePasswordField(JPasswordField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBackground(new Color(255, 255, 255)); // Cor de fundo clara
        field.setForeground(new Color(56, 56, 56)); // Texto escuro
        field.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2)); // Borda suave
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(56, 56, 56)); // Cor de fundo do botão
        button.setForeground(Color.WHITE); // Cor do texto do botão
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 40));
        button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 0, 0)); // Cor do botão ao passar o mouse
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(56, 56, 56)); // Cor original
            }
        });
    }

    private void onSave(MainFrame mainFrame) {
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Call service to change password (e.g., userService.changePassword)
        User updatedUser = userService.changePassword(UserSession.getInstance().getUser().getEmail(), currentPassword, newPassword);
        UserSession.getInstance().setUser(updatedUser);
        JOptionPane.showMessageDialog(this, "Password successfully changed!", "Success", JOptionPane.INFORMATION_MESSAGE);

        // Navigate back
        mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN);
    }
}
