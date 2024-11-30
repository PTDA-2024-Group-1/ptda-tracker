package com.ptda.tracker.ui.user.screens;

import javax.swing.*;
import java.awt.*;

import static com.ptda.tracker.config.AppConfig.APP_NAME;
import static com.ptda.tracker.config.AppConfig.LOGO_PATH;

public class CustomSplashScreen extends JFrame {

    public CustomSplashScreen() {
        setTitle(APP_NAME + " - Loading");
        setSize(350, 350);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setLayout(new BorderLayout());
        setBackground(new Color(250, 250, 250, 200)); // Set background to transparent

        // Logo
        Image scaledImage = new ImageIcon(LOGO_PATH).getImage().getScaledInstance(300, 270, Image.SCALE_SMOOTH);
        ImageIcon logo = new ImageIcon(scaledImage);
        JLabel logoLabel = new JLabel(logo);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(logoLabel, BorderLayout.CENTER);

        // Loading message
//        JLabel loadingLabel = new JLabel(APP_NAME + " is loading...");
//        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        add(loadingLabel, BorderLayout.SOUTH);
    }

    public void showSplashScreen() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    public void hideSplashScreen() {
        setVisible(false);
        dispose();
    }
}
