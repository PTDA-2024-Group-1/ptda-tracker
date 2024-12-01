package com.ptda.tracker.ui.admin.forms;

import com.ptda.tracker.models.admin.Admin;
import com.ptda.tracker.models.assistance.Assistant;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.admin.RoleManagementService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.LocaleManager;

import javax.swing.*;
import java.awt.*;

public class ManageUserForm extends JDialog {
    private final RoleManagementService roleManagementService;
    private final User user;
    private JComboBox<String> roleComboBox;

    public ManageUserForm(MainFrame mainFrame, User user) {
        super(mainFrame, TITLE, true);
        this.roleManagementService = mainFrame.getContext().getBean(RoleManagementService.class);
        this.user = user;
        initComponents();
        loadUserData();
        setLocationRelativeTo(mainFrame);
        setSize(400, 300);
    }

    private void loadUserData() {
        roleComboBox.setSelectedItem(user.getUserType());
    }

    private void saveUserRole() {
        String selectedRole = (String) roleComboBox.getSelectedItem();
        try {
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
            JOptionPane.showMessageDialog(this, USER_ROLE_UPDATED_SUCCESSFULLY);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, ERROR_UPDATING_USER_ROLE + e.getMessage(), ERROR, JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
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

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton(SAVE);
        saveButton.addActionListener(e -> saveUserRole());
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton(CANCEL);
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

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