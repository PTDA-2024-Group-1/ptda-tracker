package com.ptda.tracker.ui.screens;

import com.ptda.tracker.TrackerApplication;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.forms.LoginForm;
import com.ptda.tracker.ui.views.ProfileView;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.prefs.Preferences;

public class NavigationMenu extends JPanel {
    private final MainFrame mainFrame;
    private String currentScreen;

    private static final String HOME_SCREEN = ScreenNames.HOME_SCREEN;
    private static final String BUDGETS_SCREEN = ScreenNames.BUDGETS_SCREEN;
    private static final String PROFILE_SCREEN = ScreenNames.PROFILE_SCREEN;
    private static final String NAVIGATION_SCREEN = ScreenNames.NAVIGATION_SCREEN;
    private static final String LOGIN_SCREEN = ScreenNames.LOGIN_FORM;

    public NavigationMenu(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.currentScreen = "";

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Color.LIGHT_GRAY);

        // Top buttons
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        addButton(topPanel, "Home", e -> navigateToScreen(HOME_SCREEN, () -> new HomeScreen(mainFrame)));
        addButton(topPanel, "Budgets", e -> navigateToScreen(BUDGETS_SCREEN, () -> new BudgetsScreen(mainFrame)));
        add(topPanel, BorderLayout.NORTH);

        // Bottom buttons
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        addButton(bottomPanel, "Profile", e -> navigateToScreen(PROFILE_SCREEN, () -> new ProfileView(mainFrame)));
        addButton(bottomPanel, "Logout", e -> logout());
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addButton(JPanel panel, String label, Consumer<ActionEvent> action) {
        JButton button = new JButton(label);
        button.addActionListener(action::accept);
        panel.add(button);
    }


    private void navigateToScreen(String screenName, Supplier<JPanel> screenSupplier) {
        NavigationScreen navigationScreen = (NavigationScreen) mainFrame.getScreen(NAVIGATION_SCREEN);
        navigationScreen.setContent(screenName, screenSupplier);
        mainFrame.showScreen(NAVIGATION_SCREEN);
        updateActiveScreen(screenName);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Preferences preferences = Preferences.userNodeForPackage(TrackerApplication.class);
            preferences.remove("email");
            preferences.remove("password");
            UserSession.getInstance().clear();
            mainFrame.registerScreen(LOGIN_SCREEN, new LoginForm(mainFrame));
            mainFrame.showScreen(LOGIN_SCREEN);
        }
    }

    public void updateActiveScreen(String activeScreen) {
        this.currentScreen = activeScreen;

        // Highlight the active button
        for (Component component : getComponents()) {
            if (component instanceof JPanel) {
                for (Component innerComponent : ((JPanel) component).getComponents()) {
                    if (innerComponent instanceof JButton button) {
                        button.setEnabled(!button.getActionCommand().equals(currentScreen));
                    }
                }
            }
        }
    }

}
