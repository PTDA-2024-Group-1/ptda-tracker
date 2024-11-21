package com.ptda.tracker.ui.screens;

import com.ptda.tracker.ui.MainFrame;

import javax.swing.*;
import java.awt.*;

public class HomeScreen extends JPanel {
    public HomeScreen(MainFrame mainFrame) {
        setLayout(new BorderLayout());

        // Main content
        JLabel label = new JLabel("Welcome to the Home Screen!", SwingConstants.CENTER);
        add(label, BorderLayout.CENTER);
    }
}
