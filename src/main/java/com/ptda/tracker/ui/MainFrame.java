package com.ptda.tracker.ui;

import com.ptda.tracker.TrackerApplication;
import com.ptda.tracker.config.AppConfig;
import com.ptda.tracker.theme.ThemeManager;
import com.ptda.tracker.ui.dialogs.AboutDialog;
import com.ptda.tracker.ui.dialogs.ChooseLanguageDialog;
import com.ptda.tracker.util.LocaleManager;
import lombok.Getter;
import org.springframework.context.ApplicationContext;

import javax.security.auth.RefreshFailedException;
import javax.security.auth.Refreshable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.prefs.Preferences;

public class MainFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private JCheckBoxMenuItem lightTheme, darkTheme;
    private final ThemeManager themeManager;

    private final Map<String, JPanel> screens;
    @Getter
    private final ApplicationContext context;
    @Getter
    private String currentScreen;

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

    public MainFrame(ApplicationContext context) {
        this.context = context; // Inject Spring context
        this.screens = new HashMap<>();
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);

        // Set locale
        Preferences preferences = Preferences.userNodeForPackage(TrackerApplication.class);
        Locale locale = new Locale(preferences.get("language", "en"), preferences.get("country", "US"));
        LocaleManager.getInstance().setLocale(locale);

        setJMenuBar(createMenuBar());
        themeManager = new ThemeManager(this);
        themeManager.setTheme(getThemePreference());
        if (themeManager.isDark()) {
            darkTheme.setSelected(true);
        } else {
            lightTheme.setSelected(true);
        }

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
            JOptionPane.showMessageDialog(this, SCREEN_NOT_FOUND + ": " + name, ERROR, JOptionPane.ERROR_MESSAGE);
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

    private JMenuBar createMenuBar() {
        // Create the menu bar
        JMenuBar menuBar = new JMenuBar();

        // Create "File" menu
        JMenu fileMenu = new JMenu(FILE);
        JMenuItem languageMenuItem = new JMenuItem(LANGUAGE);
        languageMenuItem.addActionListener(e -> new ChooseLanguageDialog(this).setVisible(true));
        fileMenu.add(languageMenuItem);
        JMenuItem exitMenuItem = new JMenuItem(EXIT);
        exitMenuItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitMenuItem);

        // Create "Theme" menu
        JMenu themeMenu = new JMenu(THEME);

        lightTheme = new JCheckBoxMenuItem();
        lightTheme.addActionListener(this::lightThemeClicked);
        lightTheme.setText(LIGHT);
        themeMenu.add(lightTheme);

        darkTheme = new JCheckBoxMenuItem();
        darkTheme.addActionListener(this::darkThemeClicked);
        darkTheme.setText(DARK);
        themeMenu.add(darkTheme);

        // Create "Help" menu
        JMenu helpMenu = new JMenu(HELP);
        JMenuItem aboutMenuItem = new JMenuItem(ABOUT);
        aboutMenuItem.addActionListener(e -> new AboutDialog(this).setVisible(true));
        helpMenu.add(aboutMenuItem);

        // Add menus to the menu bar
        menuBar.add(fileMenu);
        menuBar.add(themeMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private void lightThemeClicked(ActionEvent e) {
        lightTheme.setSelected(true);
        darkTheme.setSelected(false);
        themeManager.setTheme(AppConfig.DEFAULT_LIGHT_THEME);
        setThemePreference(AppConfig.DEFAULT_LIGHT_THEME);
    }

    private void darkThemeClicked(ActionEvent e) {
        lightTheme.setSelected(false);
        darkTheme.setSelected(true);
        themeManager.setTheme(AppConfig.DEFAULT_DARK_THEME);
        setThemePreference(AppConfig.DEFAULT_DARK_THEME);
    }

    private void setThemePreference(String theme) {
        Preferences preferences = Preferences.userNodeForPackage(TrackerApplication.class);
        preferences.put("theme", theme);
    }

    private String getThemePreference() {
        Preferences preferences = Preferences.userNodeForPackage(TrackerApplication.class);
        return preferences.get("theme", AppConfig.DEFAULT_LIGHT_THEME);
    }
}