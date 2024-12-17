package com.ptda.tracker.ui;

import com.ptda.tracker.config.AppConfig;
import com.ptda.tracker.theme.ThemeManager;
import com.ptda.tracker.ui.user.dialogs.AboutDialog;
import com.ptda.tracker.ui.user.dialogs.ChooseLanguageDialog;
import com.ptda.tracker.util.ImageResourceManager;
import com.ptda.tracker.util.LocaleManager;

import lombok.Getter;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

public class MainFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private JCheckBoxMenuItem lightTheme, darkTheme;
    private final ThemeManager themeManager;
    private JLabel logoLabel;

    private final Map<String, JPanel> screens;
    @Getter
    private final ApplicationContext context;
    @Getter
    private String currentScreen;

    public MainFrame(ApplicationContext context) {
        this.context = context;
        this.screens = new HashMap<>();
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);

        // Initialize ThemeManager
        themeManager = new ThemeManager(this);
        themeManager.setTheme(getThemePreference());

        // Initialize Logo
        logoLabel = new JLabel();
        updateLogoImage();

        // Menu setup
        setJMenuBar(createMenuBar());
        setupThemeSelection();

        setTitle(TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);

        add(logoLabel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * Updates the logo image asynchronously based on the current theme.
     */
    public void updateLogoImage() {
        System.out.println("Updating logo image based on theme...");
        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
            @Override
            protected ImageIcon doInBackground() {
                System.out.println("Fetching theme-based logo...");
                return ImageResourceManager.getThemeBasedIcon(themeManager.isDark());
            }

            @Override
            protected void done() {
                try {
                    ImageIcon logoIcon = get();
                    if (logoIcon != null) {
                        logoLabel.setIcon(logoIcon);
                        System.out.println("Logo updated successfully.");
                    } else {
                        System.err.println("Failed to load logo icon.");
                    }
                } catch (Exception e) {
                    System.err.println("Error updating logo image: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    /**
     * Registers a screen with a unique name and associated JPanel.
     */
    public void registerScreen(String name, JPanel screen) {
        screens.put(name, screen);
        mainPanel.add(screen, name);
    }

    /**
     * Displays a screen by its unique name.
     */
    public void showScreen(String name) {
        if (screens.containsKey(name)) {
            cardLayout.show(mainPanel, name);
            this.currentScreen = name;
        } else {
            JOptionPane.showMessageDialog(this, SCREEN_NOT_FOUND + ": " + name, ERROR, JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Creates the application menu bar.
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu(FILE);
        JMenuItem languageMenuItem = new JMenuItem(LANGUAGE);
        languageMenuItem.addActionListener(e -> new ChooseLanguageDialog(this).setVisible(true));
        fileMenu.add(languageMenuItem);

        JMenuItem exitMenuItem = new JMenuItem(EXIT);
        exitMenuItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitMenuItem);

        JMenu themeMenu = new JMenu(THEME);
        lightTheme = new JCheckBoxMenuItem(LIGHT);
        darkTheme = new JCheckBoxMenuItem(DARK);

        lightTheme.addActionListener(this::lightThemeClicked);
        darkTheme.addActionListener(this::darkThemeClicked);

        themeMenu.add(lightTheme);
        themeMenu.add(darkTheme);

        JMenu helpMenu = new JMenu(HELP);
        JMenuItem aboutMenuItem = new JMenuItem(ABOUT);
        aboutMenuItem.addActionListener(e -> new AboutDialog(this).setVisible(true));
        helpMenu.add(aboutMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(themeMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    /**
     * Handles the light theme selection.
     */
    private void lightThemeClicked(ActionEvent e) {
        lightTheme.setSelected(true);
        darkTheme.setSelected(false);
        themeManager.setTheme(AppConfig.DEFAULT_LIGHT_THEME);
        setThemePreference(AppConfig.DEFAULT_LIGHT_THEME);
        updateLogoImage();
    }

    /**
     * Handles the dark theme selection.
     */
    private void darkThemeClicked(ActionEvent e) {
        lightTheme.setSelected(false);
        darkTheme.setSelected(true);
        themeManager.setTheme(AppConfig.DEFAULT_DARK_THEME);
        setThemePreference(AppConfig.DEFAULT_DARK_THEME);
        updateLogoImage();
    }

    /**
     * Saves the theme preference in the user preferences.
     */
    private void setThemePreference(String theme) {
        Preferences preferences = Preferences.userNodeForPackage(MainFrame.class);
        preferences.put("theme", theme);
    }

    /**
     * Retrieves the theme preference from user preferences.
     */
    private String getThemePreference() {
        Preferences preferences = Preferences.userNodeForPackage(MainFrame.class);
        return preferences.get("theme", AppConfig.DEFAULT_LIGHT_THEME);
    }

    /**
     * Sets the initial selection for the theme menu.
     */
    private void setupThemeSelection() {
        if (themeManager.isDark()) {
            darkTheme.setSelected(true);
        } else {
            lightTheme.setSelected(true);
        }
    }

    // Localization constants
    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            SCREEN_NOT_FOUND = localeManager.getTranslation("screenNotFound"),
            ERROR = localeManager.getTranslation("error"),
            TITLE = localeManager.getTranslation("divi_expense_tracker"),
            LANGUAGE = localeManager.getTranslation("language"),
            FILE = localeManager.getTranslation("file"),
            EXIT = localeManager.getTranslation("exit"),
            THEME = localeManager.getTranslation("theme"),
            LIGHT = localeManager.getTranslation("light"),
            DARK = localeManager.getTranslation("dark"),
            HELP = localeManager.getTranslation("help"),
            ABOUT = localeManager.getTranslation("about");
}
