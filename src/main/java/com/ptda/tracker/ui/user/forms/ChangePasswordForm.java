package com.ptda.tracker.ui.user.forms;

import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.user.UserService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.LocaleManager;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.swing.*;
import java.awt.*;

public class ChangePasswordForm extends JPanel {
    private final MainFrame mainFrame;

    public ChangePasswordForm(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
        setListeners();
    }

    private void setListeners() {
        cancelButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN));
        saveButton.addActionListener(e -> onSave(mainFrame));
    }

    private void onSave(MainFrame mainFrame) {
        // Get form values
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validate form
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, ALL_FIELDS_REQUIRED, ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, PASSWORDS_DO_NOT_MATCH, ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }
        PasswordEncoder passwordEncoder = mainFrame.getContext().getBean(PasswordEncoder.class);
        if (!UserSession.getInstance().getUser().getPassword().equals(passwordEncoder.encode(currentPassword))) {
            JOptionPane.showMessageDialog(this, CURRENT_PASSWORD_INCORRECT, ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Call service to change password (e.g., userService.changePassword)
        UserService userService = mainFrame.getContext().getBean(UserService.class);
        User updatedUser = userService.changePassword(UserSession.getInstance().getUser().getEmail(), currentPassword, newPassword);
        UserSession.getInstance().setUser(updatedUser);
        JOptionPane.showMessageDialog(this, PASSWORD_CHANGED + "!", SUCCESS, JOptionPane.INFORMATION_MESSAGE);

        // Navigate back
        mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN);
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = new JLabel(TITLE, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Current Password
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel(CURRENT_PASSWORD + ":"), gbc);

        gbc.gridx = 1;
        currentPasswordField = new JPasswordField(20);
        formPanel.add(currentPasswordField, gbc);

        // New Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel(NEW_PASSWORD + ":"), gbc);

        gbc.gridx = 1;
        newPasswordField = new JPasswordField(20);
        formPanel.add(newPasswordField, gbc);

        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel(CONFIRM_PASSWORD + ":"), gbc);

        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        formPanel.add(confirmPasswordField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonsPanel = new JPanel(new BorderLayout());
        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        cancelButton = new JButton(CANCEL);
        leftButtonPanel.add(cancelButton);

        saveButton = new JButton(SAVE);
        rightButtonPanel.add(saveButton);

        buttonsPanel.add(leftButtonPanel, BorderLayout.WEST);
        buttonsPanel.add(rightButtonPanel, BorderLayout.EAST);

        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private JPasswordField currentPasswordField, newPasswordField, confirmPasswordField;
    private JButton saveButton, cancelButton;
    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            TITLE = localeManager.getTranslation("change_password"),
            CURRENT_PASSWORD = localeManager.getTranslation("current_password"),
            NEW_PASSWORD = localeManager.getTranslation("new_password"),
            CONFIRM_PASSWORD = localeManager.getTranslation("confirm_password"),
            CANCEL = localeManager.getTranslation("cancel"),
            SAVE = localeManager.getTranslation("save"),
            ALL_FIELDS_REQUIRED = localeManager.getTranslation("all_fields_required"),
            PASSWORDS_DO_NOT_MATCH = localeManager.getTranslation("passwords_do_not_match"),
            CURRENT_PASSWORD_INCORRECT = localeManager.getTranslation("current_password_incorrect"),
            PASSWORD_CHANGED = localeManager.getTranslation("password_changed"),
            SUCCESS = localeManager.getTranslation("success"),
            ERROR = localeManager.getTranslation("error");
}