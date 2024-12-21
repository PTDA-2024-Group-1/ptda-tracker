package com.ptda.tracker.ui.admin.dialogs;

import com.ptda.tracker.models.admin.Admin;
import com.ptda.tracker.models.assistance.Assistant;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.administration.RoleManagementService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.admin.screens.AdministrationOptionsScreen;
import com.ptda.tracker.ui.admin.views.ManageUserView;
import com.ptda.tracker.util.LocaleManager;
import com.ptda.tracker.util.ScreenNames;

import javax.swing.*;
import java.awt.*;

public class ManageUserDialog extends JDialog {
    private MainFrame mainFrame;
    private final RoleManagementService roleManagementService;
    private final User user;
    private final Runnable onFormSubmit;
    private final AdministrationOptionsScreen adminOptionsScreen;
    private JComboBox<String> roleComboBox;

    public ManageUserDialog(MainFrame mainFrame, Runnable onFormSubmit, User user, AdministrationOptionsScreen adminOptionsScreen) {
        this.mainFrame = mainFrame;
        this.roleManagementService = mainFrame.getContext().getBean(RoleManagementService.class);
        this.user = user;
        this.onFormSubmit = onFormSubmit;
        this.adminOptionsScreen = adminOptionsScreen;
        initComponents();
        loadUserData();
        setLocationRelativeTo(mainFrame);
    }

    private void loadUserData() {
        roleComboBox.setSelectedItem(user.getUserType());
        emailVerifiedCheckBox.setSelected(user.isEmailVerified());
        activeCheckBox.setSelected(user.isActive());
    }

    private void saveUserDetails() {
        String selectedRole = (String) roleComboBox.getSelectedItem();
        try {
            if (!selectedRole.equals(user.getUserType())) {
                switch (selectedRole) {
                    case "ASSISTANT" -> roleManagementService.promoteUserToAssistant(user);
                    case "ADMIN" -> roleManagementService.promoteUserToAdmin(user);
                    case "USER" -> {
                        if (user instanceof Assistant) {
                            roleManagementService.demoteAssistant((Assistant) user);
                        } else if (user instanceof Admin) {
                            roleManagementService.demoteAdminToUser((Admin) user);
                        }
                    }
                }
            }

            User reloadedUser = roleManagementService.findUserById(user.getId());
            reloadedUser.setEmailVerified(emailVerifiedCheckBox.isSelected());
            reloadedUser.setActive(activeCheckBox.isSelected());
            roleManagementService.updateUser(reloadedUser);

            JOptionPane.showMessageDialog(this, USER_ROLE_UPDATED_SUCCESSFULLY);
            if (onFormSubmit != null) onFormSubmit.run();
            mainFrame.registerAndShowScreen(ScreenNames.MANAGE_USER_VIEW, new ManageUserView(mainFrame, adminOptionsScreen));
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, ERROR_UPDATING_USER_ROLE + e.getMessage(), ERROR, JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        setTitle(TITLE);
        setSize(400, 400);
        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel(ID));
        JTextField idField = new JTextField(String.valueOf(user.getId()));
        idField.setEditable(false);
        formPanel.add(idField);

        formPanel.add(new JLabel(NAME));
        JTextField nameField = new JTextField(user.getName());
        nameField.setEditable(false);
        formPanel.add(nameField);

        formPanel.add(new JLabel(EMAIL));
        JTextField emailField = new JTextField(user.getEmail());
        emailField.setEditable(false);
        formPanel.add(emailField);

        formPanel.add(new JLabel(ROLE));
        roleComboBox = new JComboBox<>(new String[]{"USER", "ASSISTANT", "ADMIN"});
        formPanel.add(roleComboBox);

        formPanel.add(new JLabel("Email Verified"));
        emailVerifiedCheckBox = new JCheckBox();
        formPanel.add(emailVerifiedCheckBox);

        formPanel.add(new JLabel("Active"));
        activeCheckBox = new JCheckBox();
        formPanel.add(activeCheckBox);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton(SAVE);
        saveButton.addActionListener(e -> saveUserDetails());
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton(CANCEL);
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JCheckBox emailVerifiedCheckBox;
    private JCheckBox activeCheckBox;

    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            TITLE = localeManager.getTranslation("manage_user"),
            ID = localeManager.getTranslation("id"),
            NAME = localeManager.getTranslation("name"),
            EMAIL = localeManager.getTranslation("email"),
            ROLE = localeManager.getTranslation("role"),
            SAVE = localeManager.getTranslation("save"),
            CANCEL = localeManager.getTranslation("cancel"),
            USER_ROLE_UPDATED_SUCCESSFULLY = localeManager.getTranslation("user_role_updated_successfully"),
            ERROR_UPDATING_USER_ROLE = localeManager.getTranslation("error_updating_user_role"),
            ERROR = localeManager.getTranslation("error");
}