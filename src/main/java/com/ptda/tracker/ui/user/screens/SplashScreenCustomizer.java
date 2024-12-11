package com.ptda.tracker.ui.user.screens;

import javax.swing.*;
import java.awt.*;

/**
 * A utility class for customizing the appearance of the splash screen container and image.
 */
public class SplashScreenCustomizer {

    /**
     * Configures the appearance of the splash screen container (JFrame).
     *
     * @param frame the JFrame to configure
     */
    public static void customizeContainer(JFrame frame) {
        frame.setSize(800, 800); // Set custom size
        frame.setUndecorated(true); // Remove borders
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setBackground(new Color(0, 0, 0, 0)); // Transparent background

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

        frame.setContentPane(layeredPane);
    }

    /**
     * Configures and returns a JLabel for the splash screen image.
     *
     * @param imagePath the path to the image file
     * @param width     the width to scale the image
     * @param height    the height to scale the image
     * @return a JLabel containing the customized image
     */
    public static JLabel createCustomImageLabel(String imagePath, int width, int height) {
        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JLabel imageLabel = new JLabel(scaledIcon);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setBounds((800 - width) / 2, (800 - height) / 2, width, height); // Center the image
        return imageLabel;
    }

    /**
     * Example main method for testing the splash screen.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Splash Screen Test");
            customizeContainer(frame);

            // Create the image (divi.png) and set it fully transparent
            JLabel imageLabel = createCustomImageLabel("path/to/divi.png", 350, 300);
            imageLabel.setOpaque(false);
            imageLabel.setVisible(true);

            // Add the image label to the layered pane
            JLayeredPane layeredPane = (JLayeredPane) frame.getContentPane();
            layeredPane.add(imageLabel, Integer.valueOf(0));

            // Set the image to be fully transparent
            Timer transparencyTimer = new Timer(30, null); // Trigger every 30ms
            transparencyTimer.addActionListener(e -> {
                float opacity = (float) imageLabel.getClientProperty("opacity");
                if (opacity < 1.0f) {
                    opacity += 0.01f;
                    imageLabel.putClientProperty("opacity", opacity);
                    imageLabel.repaint();
                } else {
                    transparencyTimer.stop();
                }
            });

            // Initialize opacity and start timer
            imageLabel.putClientProperty("opacity", 0.0f);
            transparencyTimer.start();

            frame.setVisible(true); // Show the splash screen
        });
    }
}
