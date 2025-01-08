package com.ptda.tracker.ui.user.screens;

import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.NavigationMenu;
import com.ptda.tracker.util.Refreshable;
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

    private static Long selectedRevisionId = null;
    private static String selectedRevisionDetails = "";

    private static NavigationScreen instance;

    public NavigationScreen(MainFrame mainFrame) {
        instance = this;

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
            if (screen instanceof Refreshable) {
                ((Refreshable) screen).refresh();
            }
            ((CardLayout) contentPanel.getLayout()).show(contentPanel, screenName);
        } else {
            // Create, add, and show the new screen
            JPanel screen = screenSupplier.get();
            contentPanel.add(screen, screenName);
            activeCards.put(screenName, screen);
            ((CardLayout) contentPanel.getLayout()).show(contentPanel, screenName);
        }
    }

    public static void setSelectedRevision(Long revisionId, String revisionDetails) {
        selectedRevisionId = revisionId;
        selectedRevisionDetails = revisionDetails;
    }
}
