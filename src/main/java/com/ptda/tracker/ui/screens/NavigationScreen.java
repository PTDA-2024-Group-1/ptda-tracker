package com.ptda.tracker.ui.screens;

import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.NavigationMenu;
import com.ptda.tracker.util.ScreenNames;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class NavigationScreen extends JPanel {
    private final JPanel contentPanel; // Area for dynamic content
    private final NavigationMenu navigationMenu;
    private final Map<String, JPanel> activeCards = new HashMap<>(); // Cache of added screens

    public NavigationScreen(MainFrame mainFrame) {
        setLayout(new BorderLayout());

        // Navigation menu
        navigationMenu = new NavigationMenu(mainFrame);
        add(navigationMenu, BorderLayout.WEST);

        // Content panel
        contentPanel = new JPanel(new CardLayout());
        add(contentPanel, BorderLayout.CENTER);

        // Set initial content
        setContent(ScreenNames.HOME_SCREEN, () -> new HomeScreen(mainFrame));
    }

    public void setContent(String screenName, Supplier<JPanel> screenSupplier) {
        if (activeCards.containsKey(screenName)) {
            // Show existing screen
            JPanel screen = activeCards.get(screenName);
            if (screen instanceof HomeScreen) {
                ((HomeScreen) screen).refreshData();
            }
            ((CardLayout) contentPanel.getLayout()).show(contentPanel, screenName);
        } else {
            // Create, add, and show the new screen
            JPanel screen = screenSupplier.get();
            contentPanel.add(screen, screenName);
            activeCards.put(screenName, screen);
            ((CardLayout) contentPanel.getLayout()).show(contentPanel, screenName);
        }

        // Highlight the active screen in the menu
        //navigationMenu.updateActiveScreen(screenName);
    }
}