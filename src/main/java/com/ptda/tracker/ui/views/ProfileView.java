package com.ptda.tracker.ui.views;

import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.tracker.BudgetAccessService;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.services.user.UserService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.NavigationMenu;
import com.ptda.tracker.ui.forms.ChangePasswordForm;
import com.ptda.tracker.ui.forms.ProfileForm;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.awt.*;

public class ProfileView extends JPanel {
    private final MainFrame mainFrame;

    public ProfileView(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initUI();
        refreshUserData();
    }

    private void refreshUserData() {
        User user = UserSession.getInstance().getUser();
        if (user != null) {
            nameLabel.setText(user.getName());
            emailLabel.setText(user.getEmail());
        }
    }

    private void deleteProfile() {
        JOptionPane.showMessageDialog(mainFrame, DELETE_PROFILE_CONFIRMATION, DELETE_PROFILE_TITLE, JOptionPane.WARNING_MESSAGE);

        User user = UserSession.getInstance().getUser();
        ApplicationContext context = mainFrame.getContext();
        if (user == null) {
            JOptionPane.showMessageDialog(mainFrame, USER_NOT_FOUND, ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }
        BudgetAccessService budgetAccessService = context.getBean(BudgetAccessService.class);
        budgetAccessService.deleteAllByUserId(user.getId());

        ExpenseService expenseService = context.getBean(ExpenseService.class);
        expenseService.deleteAllPersonalExpensesByUserId(user.getId());

        user.setName("Deleted User");
        user.setEmail("deleted");
        user.setEmailVerified(false);
        user.setPassword("deleted");
        user.setActive(false);
        UserService userService = context.getBean(UserService.class);
        userService.update(user);

        NavigationMenu.performLogout(mainFrame);
    }

    private void navigateToEditProfile() {
        mainFrame.registerAndShowScreen(ScreenNames.PROFILE_FORM, new ProfileForm(mainFrame, this::refreshUserData));
    }

    private void navigateToChangePassword() {
        mainFrame.registerAndShowScreen(ScreenNames.CHANGE_PASSWORD_FORM, new ChangePasswordForm(mainFrame));
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 20, 20, 20);

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel(MY_PROFILE, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, gbc);

        // Spacer
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(Box.createVerticalStrut(20), gbc);

        // Name
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel nameLabelText = new JLabel(NAME);
        nameLabelText.setFont(new Font("Arial", Font.PLAIN, 16));
        add(nameLabelText, gbc);

        gbc.gridx = 1;
        nameLabel = new JLabel();
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(nameLabel, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel emailLabelText = new JLabel(EMAIL);
        emailLabelText.setFont(new Font("Arial", Font.PLAIN, 16));
        add(emailLabelText, gbc);

        gbc.gridx = 1;
        emailLabel = new JLabel();
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(emailLabel, gbc);

        // Spacer
        gbc.gridy = 4;
        add(Box.createVerticalStrut(30), gbc);

        // Buttons Panel
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 5;
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Edit Profile Button
        editButton = new JButton(EDIT_PROFILE);
        editButton.addActionListener(e -> navigateToEditProfile());
        buttonsPanel.add(editButton);

        // Spacer
        buttonsPanel.add(Box.createHorizontalStrut(20));

        // Change Password Button
        changePasswordButton = new JButton(CHANGE_PASSWORD);
        changePasswordButton.addActionListener(e -> navigateToChangePassword());
        buttonsPanel.add(changePasswordButton);

        // Delete Profile Button
        deleteProfileButton = new JButton(DELETE_PROFILE);
        deleteProfileButton.addActionListener(e -> deleteProfile());
        buttonsPanel.add(deleteProfileButton);

        add(buttonsPanel, gbc);
    }

    private JLabel nameLabel, emailLabel;
    private JButton editButton, changePasswordButton, deleteProfileButton;

    private static final String
            MY_PROFILE = "My Profile",
            NAME = "Name:",
            EMAIL = "Email:",
            EDIT_PROFILE = "Edit Profile",
            CHANGE_PASSWORD = "Change Password",
            DELETE_PROFILE = "Delete Profile",
            DELETE_PROFILE_CONFIRMATION = "Are you sure you want to delete your profile?",
            DELETE_PROFILE_TITLE = "Delete Profile",
            USER_NOT_FOUND = "User not found.",
            ERROR = "Error";

}