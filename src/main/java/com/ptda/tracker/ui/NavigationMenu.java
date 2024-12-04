package com.ptda.tracker.ui;

import com.ptda.tracker.TrackerApplication;
import com.ptda.tracker.ui.admin.screens.AdministrationOptionsScreen;
import com.ptda.tracker.ui.assistant.screens.AssistanceScreen;
import com.ptda.tracker.ui.user.forms.LoginForm;
import com.ptda.tracker.ui.user.screens.*;
import com.ptda.tracker.ui.user.views.ProfileView;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

public class NavigationMenu extends JPanel {
    private final MainFrame mainFrame;

    private static final Color BACKGROUND_COLOR = new Color(56, 56, 56); // Color #383838

    public NavigationMenu(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.currentScreen = "";

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Space between buttons

        // Adds top buttons
        addButtonToPanel(topPanel, HOME, HOME_SCREEN, gbc, 0);
        addButtonToPanel(topPanel, BUDGETS, BUDGETS_SCREEN, gbc, 1);
        addButtonToPanel(topPanel, EXPENSES, EXPENSES_SCREEN, gbc, 2);
        addButtonToPanel(topPanel, SUPPORT, USER_TICKETS_SCREEN, gbc, 3);
        addButtonToPanel(topPanel, ASSISTANCE, ASSISTANCE_SCREEN, gbc, 4);
        addButtonToPanel(topPanel, ADMINISTRATION, ADMINISTRATION_OPTIONS_SCREEN, gbc, 5);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        gbc.gridy = 0; // Reset the index for the bottom panel

        // Adds bottom buttons
        addButtonToPanel(bottomPanel, PROFILE, PROFILE_SCREEN, gbc, 0);
        addButtonToPanel(bottomPanel, LOGOUT, null, gbc, 1);

        // Add the top and bottom panels to the main layout
        add(topPanel, BorderLayout.CENTER); // Main buttons in the middle
        add(bottomPanel, BorderLayout.SOUTH); // Bottom buttons at the bottom
    }

    private void addButtonToPanel(JPanel panel, String label, String screenName, GridBagConstraints gbc, int row) {
        String userType = UserSession.getInstance().getUser().getUserType();

        // Check if the user is of type "USER" before adding the assistance or administration button
        if (userType.equals("USER") && (label.equals(ASSISTANCE) || label.equals(ADMINISTRATION))) {
            return; // Do not add the button if the user is of type "USER"
        }

        // Check if the user is of type "ASSISTANT" before adding the administration button
        if (userType.equals("ASSISTANT") && label.equals(ADMINISTRATION)) {
            return; // Do not add the administration button if the user is of type "ASSISTANT"
        }

        JButton button = new JButton(label);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setActionCommand(screenName); // Button action command to check if it's the active button
        button.addActionListener(e -> {
            if (screenName != null) {
                navigateToScreen(screenName);
            } else {
                logout();
            }
        });

        // Layout settings
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(button, gbc);
    }

    private JPanel getScreenInstance(String screenName) {
        return switch (screenName) {
            case HOME_SCREEN -> new HomeScreen(mainFrame);
            case BUDGETS_SCREEN -> new BudgetsScreen(mainFrame);
            case EXPENSES_SCREEN -> new ExpensesScreen(mainFrame);
            case USER_TICKETS_SCREEN -> new UserTicketsScreen(mainFrame);
            case ASSISTANCE_SCREEN -> new AssistanceScreen(mainFrame);
            case PROFILE_SCREEN -> new ProfileView(mainFrame);
            case ADMINISTRATION_OPTIONS_SCREEN -> new AdministrationOptionsScreen(mainFrame);
            default -> new JPanel(); // Return an empty panel if the screen is not found
        };
    }

    private void navigateToScreen(String screenName) {
        NavigationScreen navigationScreen = (NavigationScreen) mainFrame.getScreen(NAVIGATION_SCREEN);
        navigationScreen.setContent(screenName, () -> getScreenInstance(screenName));
        mainFrame.showScreen(NAVIGATION_SCREEN);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                ARE_YOU_SURE,
                LOGOUT,
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            performLogout(mainFrame);
        }
    }

    public static void performLogout(MainFrame mainFrame) {
        Preferences preferences = Preferences.userNodeForPackage(TrackerApplication.class);
        preferences.remove("email");
        preferences.remove("password");
        UserSession.getInstance().clear();
        mainFrame.registerScreen(LOGIN_SCREEN, new LoginForm(mainFrame));
        mainFrame.showScreen(LOGIN_SCREEN);
    }

    public void updateActiveScreen(String activeScreen) {
        this.currentScreen = activeScreen;
        for (Component component : getComponents()) {
            if (component instanceof JPanel) {
                for (Component innerComponent : ((JPanel) component).getComponents()) {
                    if (innerComponent instanceof JButton button) {
                        // Set the background color and font size for the active button
                        if (button.getActionCommand().equals(currentScreen)) {
                            button.setFont(new Font("Arial", Font.BOLD, 16));
                        } else {
                            button.setFont(new Font("Arial", Font.BOLD, 14));
                        }
                    }
                }
            }
        }
    }

    private String currentScreen;

    private static final String
            HOME_SCREEN = ScreenNames.HOME_SCREEN,
            BUDGETS_SCREEN = ScreenNames.BUDGETS_SCREEN,
            EXPENSES_SCREEN = ScreenNames.EXPENSES_SCREEN,
            USER_TICKETS_SCREEN = ScreenNames.USER_TICKETS_SCREEN,
            PROFILE_SCREEN = ScreenNames.PROFILE_SCREEN,
            ADMINISTRATION_OPTIONS_SCREEN = ScreenNames.ADMINISTRATION_OPTIONS_SCREEN,
            NAVIGATION_SCREEN = ScreenNames.NAVIGATION_SCREEN,
            LOGIN_SCREEN = ScreenNames.LOGIN_FORM,
            ASSISTANCE_SCREEN = ScreenNames.ASSISTANCE_SCREEN;




    private static final String
            HOME = "Home",
            BUDGETS = "Budgets",
            EXPENSES = "My Expenses",
            TICKETS = "Tickets",
            SUPPORT = "Support",
            ASSISTANCE = "Assistance",
            PROFILE = "Profile",
            ADMINISTRATION = "Administration",
            LOGOUT = "Logout",
            ARE_YOU_SURE = "Are you sure you want to logout?";
}