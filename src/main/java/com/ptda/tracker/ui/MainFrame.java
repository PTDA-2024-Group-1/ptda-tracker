package com.ptda.tracker.ui;

import com.ptda.tracker.ui.screens.NavigationMenu;
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
    private String currentScreen;

    private static final String NAVIGATION_SCREEN = "navigationScreen";

    public MainFrame(ApplicationContext context) {
        this.context = context; // Inject Spring context
        this.screens = new HashMap<>();
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);

        setTitle("Swing + Spring Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
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
            if (name.equals(NAVIGATION_SCREEN)) {
                setCurrentScreen(name);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Screen not found: " + name, "Error", JOptionPane.ERROR_MESSAGE);
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
        this.currentScreen = screen;
        // Notify NavigationMenu to update highlights
        if (screens.get("navMenu") != null) {
            ((NavigationMenu) screens.get("navMenu")).updateActiveScreen(screen);
        }
    }

    public JPanel getScreen(String screenName) {
        return screens.get(screenName);
    }
}
