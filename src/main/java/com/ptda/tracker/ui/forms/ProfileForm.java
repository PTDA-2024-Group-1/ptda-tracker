package com.ptda.tracker.ui.forms;

import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.user.UserService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;

public class ProfileForm extends JPanel {
    private final MainFrame mainFrame;
    private final Runnable onSave;
    private final User user;
    private JTextField nameField, emailField;
    private JButton cancelButton, saveButton;

    private static final String
            EDIT_PROFILE = "Edit Profile",
            NAME = "Name",
            EMAIL = "Email",
            CANCEL = "Cancel",
            SAVE = "Save",
            NAME_AND_EMAIL_REQUIRED = "Name and Email are required",
            ERROR = "Error",
            SUCCESSFULLY_UPDATED = "Profile successfully updated",
            SUCCESS = "Success";

    public ProfileForm(MainFrame mainFrame, Runnable onSave) {
        this.mainFrame = mainFrame;
        this.onSave = onSave;
        user = UserSession.getInstance().getUser();
        initUI();
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
        JLabel title = new JLabel(EDIT_PROFILE, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(56, 56, 56));
        add(title, gbc);

        // Name
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel nameLabel = new JLabel(NAME + ":");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(nameLabel, gbc);

        gbc.gridx = 1;
        nameField = new JTextField(user.getName(), 20);
        styleTextField(nameField);
        add(nameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel emailLabel = new JLabel(EMAIL + ":");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(emailLabel, gbc);

        gbc.gridx = 1;
        emailField = new JTextField(user.getEmail(), 20);
        styleTextField(emailField);
        add(emailField, gbc);

        // Cancel Button
        gbc.gridx = 0;
        gbc.gridy = 3;
        cancelButton = new JButton(CANCEL);
        styleButton(cancelButton);
        add(cancelButton, gbc);

        // Save Button
        gbc.gridx = 1;
        saveButton = new JButton(SAVE);
        styleButton(saveButton);
        add(saveButton, gbc);

        setBackground(new Color(240, 240, 240));
    }

    private void setListeners() {
        cancelButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN));
        saveButton.addActionListener(e -> onSave(user, mainFrame));
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBackground(new Color(255, 255, 255));
        field.setForeground(new Color(56, 56, 56));
        field.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(56, 56, 56));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 40));
        button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Efeito hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 0, 0));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(56, 56, 56));
            }
        });
    }

    private void onSave(User user, MainFrame mainFrame) {
        UserService userService = mainFrame.getContext().getBean(UserService.class);
        String newName = nameField.getText().trim();
        String newEmail = emailField.getText().trim();

        if (newName.isEmpty() || newEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this, NAME_AND_EMAIL_REQUIRED, ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Atualiza o objeto usuário
        user.setName(newName);
        user.setEmail(newEmail);

        user = userService.update(user);

        // Salva o utilizador na sessão
        UserSession.getInstance().setUser(user);

        JOptionPane.showMessageDialog(this, SUCCESSFULLY_UPDATED + "!", SUCCESS, JOptionPane.INFORMATION_MESSAGE);

        // Retorna para a tela de navegação
        onSave.run();
        mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN);
    }
}
