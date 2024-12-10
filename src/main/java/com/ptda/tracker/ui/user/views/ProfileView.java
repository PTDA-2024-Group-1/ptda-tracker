package com.ptda.tracker.ui.user.views;

import com.ptda.tracker.models.user.User;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.forms.ChangePasswordForm;
import com.ptda.tracker.ui.user.forms.ProfileForm;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileView extends JPanel {
    private final MainFrame mainFrame;

    public ProfileView(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
        refreshUserData();
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

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);

        // Painel de Informações (User Information)
        JPanel infoPanel = new JPanel(new GridLayout(6, 1, 10, 10)); // 6 linhas para incluir os botões
        infoPanel.setBorder(BorderFactory.createTitledBorder("User Information"));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 1;
        add(infoPanel, gbc);

        // Adicionando campos de informações
        infoPanel.add(createInfoBox(" Name: ", nameLabel = new JLabel()));
        infoPanel.add(createInfoBox(" Email: ", emailLabel = new JLabel()));
        infoPanel.add(createInfoBox(" Joined: ", createdDateLabel = new JLabel()));
        infoPanel.add(createInfoBox(" Account Age: ", accountAgeLabel = new JLabel()));

        // Painel de Botões dentro do infoPanel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        infoPanel.add(buttonsPanel);

        // Adicionando botões
        JButton editButton = new JButton("Edit Profile");
        editButton.addActionListener(e -> mainFrame.registerAndShowScreen(ScreenNames.PROFILE_FORM, new ProfileForm(mainFrame, this::refreshUserData)));
        buttonsPanel.add(editButton);

        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.addActionListener(e -> mainFrame.registerAndShowScreen(ScreenNames.CHANGE_PASSWORD_FORM, new ChangePasswordForm(mainFrame)));
        buttonsPanel.add(changePasswordButton);

        JButton deleteProfileButton = new JButton("Delete Profile");
        deleteProfileButton.addActionListener(e -> deleteProfile());
        buttonsPanel.add(deleteProfileButton);
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
        JOptionPane.showMessageDialog(mainFrame, "Are you sure you want to delete your profile?", "Delete Profile", JOptionPane.WARNING_MESSAGE);

        User user = UserSession.getInstance().getUser();
        if (user != null) {
            user.setName("Deleted User");
            user.setEmail("deleted");
            user.setActive(false);
            JOptionPane.showMessageDialog(mainFrame, "Profile deleted successfully.");
        }
    }

    private String calculateAccountAge(long createdAt) {
        long currentTime = System.currentTimeMillis();
        long diffInMillis = currentTime - createdAt;
        long days = diffInMillis / (1000 * 60 * 60 * 24);
        long months = days / 30;
        long years = months / 12;

        if (years > 0) {
            return years + " year(s), " + (months % 12) + " month(s)";
        } else if (months > 0) {
            return months + " month(s), " + (days % 30) + " day(s)";
        } else {
            return days + " day(s)";
        }
    }

    private JLabel nameLabel, emailLabel, createdDateLabel, accountAgeLabel;
}
