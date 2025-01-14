package com.ptda.tracker.ui.user.forms;

import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.user.UserService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.LocaleManager;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;

public class ProfileForm extends JPanel {
    private final MainFrame mainFrame;
    private final Runnable onSave;
    private final User user;

    public ProfileForm(MainFrame mainFrame, Runnable onSave) {
        this.mainFrame = mainFrame;
        this.onSave = onSave;
        user = UserSession.getInstance().getUser();
        initComponents();
        setListeners();
    }

    private void setListeners() {
        cancelButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN));
        saveButton.addActionListener(e -> onSave(user, mainFrame));
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

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = new JLabel(EDIT_PROFILE, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel(NAME + ":"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField(user.getName(), 20);
        formPanel.add(nameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel(EMAIL + ":"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(user.getEmail(), 20);
        formPanel.add(emailField, gbc);

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

    private JTextField nameField, emailField;
    private JButton cancelButton, saveButton;
    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            EDIT_PROFILE = localeManager.getTranslation("edit_profile"),
            NAME = localeManager.getTranslation("name"),
            EMAIL = localeManager.getTranslation("email"),
            CANCEL = localeManager.getTranslation("cancel"),
            SAVE = localeManager.getTranslation("save"),
            NAME_AND_EMAIL_REQUIRED = localeManager.getTranslation("name_and_email_required"),
            ERROR = localeManager.getTranslation("error"),
            SUCCESSFULLY_UPDATED = localeManager.getTranslation("successfully_updated"),
            SUCCESS = localeManager.getTranslation("success");
}