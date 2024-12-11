package com.ptda.tracker.ui.user.screens;

import javax.swing.*;
import java.awt.*;

public class CustomSplashScreen extends JFrame {

    public CustomSplashScreen() {
        // Set up the main frame properties
        setTitle("Divi - Loading");
        setSize(800, 800); // Adjust the frame size
        setLocationRelativeTo(null);
        setUndecorated(true); // Remove borders
        setBackground(new Color(0, 0, 0, 0)); // Set transparent background

        // Configure a transparent layered pane
        JLayeredPane layeredPane = new JLayeredPane() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setComposite(AlphaComposite.SrcOver);
                g2d.setColor(new Color(0, 0, 0, 0)); // Fully transparent background
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        layeredPane.setPreferredSize(new Dimension(800, 800));

        // Add the foreground image (divi_1.png) scaled to 2x the original size
        JLabel foregroundLabel = createTransparentImageLabel("src/main/resources/images/divi_1.png", 700, 600);
        foregroundLabel.setBounds(50, 100, 700, 600); // Adjust bounds to center the larger logo
        layeredPane.add(foregroundLabel, Integer.valueOf(1)); // Add foreground at the top layer

        // Set the layeredPane as the content pane
        setContentPane(layeredPane);
    }

    /**
     * Configures and returns a JLabel for a splash screen image.
     *
     * @param imagePath the path to the image file
     * @param width     the width to scale the image
     * @param height    the height to scale the image
     * @return a JLabel containing the customized transparent image
     */
    private JLabel createTransparentImageLabel(String imagePath, int width, int height) {
        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JLabel imageLabel = new JLabel(scaledIcon);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
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

            // Simulate loading process
            try {
                Thread.sleep(5000); // Keep splash screen visible for 5 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            splashScreen.hideSplashScreen();
        });
    }
}
