package com.ptda.tracker.ui.user.screens;

import com.ptda.tracker.TrackerApplication;
import com.ptda.tracker.util.LocaleManager;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.prefs.Preferences;

public class CustomSplashScreen extends JFrame {

    public CustomSplashScreen() {
        // Set the LocaleManager before building the UI
        setCurrentLocaleFromPreferences();

        // Set up the main frame properties
        setTitle("Divi - Loading");
        setSize(800, 800);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        JLayeredPane layeredPane = new JLayeredPane() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setComposite(AlphaComposite.SrcOver);
                g2d.setColor(new Color(0, 0, 0, 0));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        layeredPane.setPreferredSize(new Dimension(800, 800));

        JLabel foregroundLabel = createTransparentImageLabel("src/main/resources/images/divi_1.png", 700, 600);
        foregroundLabel.setBounds(50, 100, 700, 600); 
        layeredPane.add(foregroundLabel, Integer.valueOf(1));

        JLabel greetingLabel = new JLabel(LocaleManager.getInstance().getTranslation("greeting"));
        greetingLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        greetingLabel.setVerticalAlignment(SwingConstants.TOP);
        greetingLabel.setForeground(Color.BLACK);
        greetingLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        greetingLabel.setBounds(300, 20, 100, 50);
        foregroundLabel.add(greetingLabel);

        setContentPane(layeredPane);
    }

    private void setCurrentLocaleFromPreferences() {
        Preferences preferences = Preferences.userNodeForPackage(TrackerApplication.class);
        String language = preferences.get("language", "en");
        String country = preferences.get("country", "US");
        Locale userLocale = new Locale(language, country);
        LocaleManager.getInstance().setLocale(userLocale);
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
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    public void hideSplashScreen() {
        setVisible(false);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CustomSplashScreen splashScreen = new CustomSplashScreen();
            splashScreen.showSplashScreen();

            try {
                Thread.sleep(5000); // Keep splash screen visible for 5 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            splashScreen.hideSplashScreen();
        });
    }
}
