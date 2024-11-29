package com.ptda.tracker.ui;

import lombok.Getter;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final Map<String, JPanel> screens;
    @Getter
    private final ApplicationContext context;
    @Getter
    private String currentScreen;

    private static final String
            NAVIGATION_SCREEN = "navigationScreen",
            SCREEN_NOT_FOUND = "Screen not found: ",
            ERROR = "Error",
            TITLE = "Divi - Turn your expenses into Achievements.";

    public MainFrame(ApplicationContext context) {
        this.context = context; // Inject Spring context
        this.screens = new HashMap<>();
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);

        setTitle(TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);
        add(mainPanel);
    }

    public void registerScreen(String name, JPanel screen) {
        screens.put(name, screen);
        mainPanel.add(screen, name);
    }

    public void showScreen(String name) {
        if (screens.containsKey(name)) {
            cardLayout.show(mainPanel, name);
            this.currentScreen = name;
        } else {
            JOptionPane.showMessageDialog(this, SCREEN_NOT_FOUND + name, ERROR, JOptionPane.ERROR_MESSAGE);
        }
    }

    public void registerAndShowScreen(String name, JPanel screen) {
        registerScreen(name, screen);
        showScreen(name);
    }

    public void removeScreen(String screenName) {
        screens.remove(screenName);
    }

    public void setCurrentScreen(String screen) {
        // Notify NavigationMenu to update highlights
        if (screens.get("navMenu") != null) {
            ((NavigationMenu) screens.get("navMenu")).updateActiveScreen(screen);
        }
    }

    public JPanel getScreen(String screenName) {
        return screens.get(screenName);
    }
}