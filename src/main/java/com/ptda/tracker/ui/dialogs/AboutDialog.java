package com.ptda.tracker.ui.dialogs;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import static com.ptda.tracker.config.AppConfig.*;

public class AboutDialog extends JDialog {

    public AboutDialog(Window owner) {
        super(owner);
        setTitle("About");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setResizable(false);

        initComponents();
        pack();
        setLocationRelativeTo(owner);
    }

    private void okClicked(ActionEvent e) {
        dispose(); // Close the dialog
    }

    private void openUrl(String url) {
        try {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(new URI(url));
            } else {
                JOptionPane.showMessageDialog(this, "URL opening is not supported on your system.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to open URL: " + url, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        // Main container with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().add(mainPanel);

        // Content Panel (Center)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Layout constraints for GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Icon
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        ImageIcon appLogo = new ImageIcon(LOGO_PATH);
        Image scaledImage = appLogo.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
        contentPanel.add(logoLabel, gbc);

        // Application Name
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel appName = new JLabel(APP_NAME);
        appName.setFont(new Font("Arial", Font.BOLD, 18));
        contentPanel.add(appName, gbc);

        // Version Info
        gbc.gridy = 1;
        JLabel versionInfo = new JLabel("Version 1.0.12345");
        versionInfo.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPanel.add(versionInfo, gbc);

        // Java Runtime Info
        gbc.gridy = 2;
        JLabel javaRuntime = new JLabel("Java runtime: " + Runtime.version());
        javaRuntime.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPanel.add(javaRuntime, gbc);

        // Website Link
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JLabel websiteLink = new JLabel("<html><a href='#'>Visit our website</a></html>");
        websiteLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        websiteLink.setForeground(Color.BLUE);
        websiteLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openUrl(HOME_URL);
            }
        });
        contentPanel.add(websiteLink, gbc);

        // Copyright Info
        gbc.gridy = 5;
        JLabel copyright = new JLabel(COPYRIGHT_DETAILS);
        copyright.setFont(new Font("Arial", Font.PLAIN, 12));
        contentPanel.add(copyright, gbc);

        // Button Panel (South)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Arial", Font.PLAIN, 14));
        okButton.addActionListener(this::okClicked);
        buttonPanel.add(okButton);
    }
}
