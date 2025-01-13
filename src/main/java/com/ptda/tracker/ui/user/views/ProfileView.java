package com.ptda.tracker.ui.user.views;

import com.ptda.tracker.models.user.User;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.dialogs.SummaryDialog;
import com.ptda.tracker.ui.user.forms.ChangePasswordForm;
import com.ptda.tracker.ui.user.forms.ProfileForm;
import com.ptda.tracker.util.LocaleManager;
import com.ptda.tracker.util.Refreshable;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileView extends JPanel implements Refreshable {
    private final MainFrame mainFrame;

    public ProfileView(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
        refreshUserData();
        setListeners();
    }

    private void refreshUserData() {
        User user = UserSession.getInstance().getUser();
        if (user != null) {
            nameLabel.setText(user.getName());
            emailLabel.setText(user.getEmail());
            createdDateLabel.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date(user.getCreatedAt())));
            accountAgeLabel.setText(calculateAccountAge(user.getCreatedAt()));
        }
    }

    private void setListeners() {
        summaryButton.addActionListener(e -> new SummaryDialog(mainFrame).setVisible(true));
        editButton.addActionListener(e -> mainFrame.registerAndShowScreen(ScreenNames.PROFILE_FORM, new ProfileForm(mainFrame, this::refreshUserData)));
        changePasswordButton.addActionListener(e -> mainFrame.registerAndShowScreen(ScreenNames.CHANGE_PASSWORD_FORM, new ChangePasswordForm(mainFrame)));
        deleteProfileButton.addActionListener(e -> deleteProfile());
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);

        // Painel de Informações (User Information)
        JPanel infoPanel = new JPanel(new GridLayout(6, 1, 10, 10)); // 6 linhas para incluir os botões
        infoPanel.setBorder(BorderFactory.createTitledBorder(USER_INFORMATION));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 1;
        add(infoPanel, gbc);

        // Adicionando campos de informações
        infoPanel.add(createInfoBox(NAME + " : ", nameLabel = new JLabel()));
        infoPanel.add(createInfoBox(EMAIL+ " : ", emailLabel = new JLabel()));
        infoPanel.add(createInfoBox(JOINED + " : ", createdDateLabel = new JLabel()));
        infoPanel.add(createInfoBox(ACCOUNT_AGE + " : ", accountAgeLabel = new JLabel()));

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        summaryButton = new JButton(SUMMARY);
        editButton = new JButton(EDIT_PROFILE);
        changePasswordButton = new JButton(CHANGE_PASSWORD);
        deleteProfileButton = new JButton(DELETE_PROFILE);

        buttonsPanel.add(summaryButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(changePasswordButton);
        buttonsPanel.add(deleteProfileButton);
        infoPanel.add(buttonsPanel);
    }

    // Métod.o auxiliar para criar uma linha de informação simples
    private JPanel createInfoBox(String labelText, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        panel.add(label, BorderLayout.WEST);
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }


    private void deleteProfile() {
        JOptionPane.showMessageDialog(mainFrame, MESSAGE_DELETE_PROFILE, DELETE_PROFILE_TITLE, JOptionPane.WARNING_MESSAGE);

        User user = UserSession.getInstance().getUser();
        if (user != null) {
            user.setName("Deleted User");
            user.setEmail("deleted");
            user.setActive(false);
            JOptionPane.showMessageDialog(mainFrame, DELETE_PROFILE_SUCCESS);
        }
    }

    private String calculateAccountAge(long createdAt) {
        long currentTime = System.currentTimeMillis();
        long diffInMillis = currentTime - createdAt;
        long days = diffInMillis / (1000 * 60 * 60 * 24);
        long months = days / 30;
        long years = months / 12;

        if (years > 0) {
            return years + " " + YEARS + (months % 12) + " " + MONTHS;
        } else if (months > 0) {
            return months + " " + MONTHS + (days % 30) + " " + DAYS;
        } else {
            return days + " " + DAYS;
        }
    }

    private JLabel nameLabel, emailLabel, createdDateLabel, accountAgeLabel;
    private JButton summaryButton, editButton, changePasswordButton, deleteProfileButton;

    @Override
    public void refresh() {
        refreshUserData();
    }

    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            USER_INFORMATION = localeManager.getTranslation("user_information"),
            NAME = localeManager.getTranslation("name"),
            EMAIL = localeManager.getTranslation("email"),
            JOINED = localeManager.getTranslation("joined"),
            ACCOUNT_AGE = localeManager.getTranslation("account_age"),
            SUMMARY = localeManager.getTranslation("summary"),
            EDIT_PROFILE = localeManager.getTranslation("edit_profile"),
            CHANGE_PASSWORD = localeManager.getTranslation("change_password"),
            DELETE_PROFILE = localeManager.getTranslation("delete_profile"),
            MESSAGE_DELETE_PROFILE = localeManager.getTranslation("message_delete_profile"),
            DELETE_PROFILE_TITLE = localeManager.getTranslation("delete_profile_title"),
            DELETE_PROFILE_SUCCESS = localeManager.getTranslation("delete_profile_success"),
            YEARS = localeManager.getTranslation("years"),
            MONTHS = localeManager.getTranslation("months"),
            DAYS = localeManager.getTranslation("days");
}
