package com.ptda.tracker.ui.dialogs;

import com.ptda.tracker.models.user.User;

import javax.swing.*;

public class ProfileDialog extends JDialog {
    private final User user;

    public ProfileDialog(User user) {
        this.user = user;
        initUI();
    }

    private void initUI() {
        setTitle("Profile");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel("Name: " + user.getName());
        JLabel emailLabel = new JLabel("Email: " + user.getEmail());

        panel.add(nameLabel);
        panel.add(emailLabel);

        add(panel);
    }
}
