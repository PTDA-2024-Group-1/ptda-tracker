package com.ptda.tracker.ui.user.screens;

import com.ptda.tracker.TrackerApplication;
import com.ptda.tracker.util.LocaleManager;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.prefs.Preferences;

public class CustomSplashScreen extends JDialog {

    public CustomSplashScreen() {
        setUndecorated(true);
        Color backgroundColor = new Color(0, 0, 0, 0);
        setBackground(backgroundColor);
        JLayeredPane layeredPane = new JLayeredPane() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setComposite(AlphaComposite.SrcOver);
                g2d.setColor(backgroundColor);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        layeredPane.setPreferredSize(new Dimension(800, 800));

        JLabel foregroundLabel = createTransparentImageLabel("src/main/resources/images/divi_splash.png", 700, 600);
        foregroundLabel.setBounds(50, 100, 700, 600); 
        layeredPane.add(foregroundLabel, Integer.valueOf(1));

        JLabel greetingLabel = new JLabel(LocaleManager.getInstance().getTranslation("divi_is_loading") + "...");
        greetingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        greetingLabel.setVerticalAlignment(SwingConstants.CENTER);
        greetingLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Add 20 pixels margin at the bottom
        foregroundLabel.setLayout(new BorderLayout());
        foregroundLabel.add(greetingLabel, BorderLayout.SOUTH);

        setContentPane(layeredPane);
        pack();
        setLocationRelativeTo(null);
    }

    private JLabel createTransparentImageLabel(String imagePath, int width, int height) {
        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JLabel imageLabel = new JLabel(scaledIcon);
        imageLabel.setLayout(null);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setBackground(Color.WHITE);
        imageLabel.setOpaque(true);
        return imageLabel;
    }

    public void showSplashScreen() {
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
            toFront();
        });
    }

    public void hideSplashScreen() {
        SwingUtilities.invokeLater(() -> {
            setVisible(false);
            dispose();
        });
    }
}
