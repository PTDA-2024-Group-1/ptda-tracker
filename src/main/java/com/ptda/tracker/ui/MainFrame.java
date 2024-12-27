package com.ptda.tracker.ui;

import com.ptda.tracker.config.AppConfig;
import com.ptda.tracker.theme.ThemeManager;
import com.ptda.tracker.ui.user.dialogs.AboutDialog;
import com.ptda.tracker.ui.user.dialogs.ChooseLanguageDialog;
import com.ptda.tracker.util.ImageResourceManager;
import com.ptda.tracker.util.LocaleManager;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext; // <-- Added import

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
        this.context = context;
        this.screens = new HashMap<>();
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);

        themeManager = new ThemeManager(this);
        themeManager.setTheme(getThemePreference());

        setJMenuBar(createMenuBar());

        if (themeManager.isDark()) {
            darkTheme.setSelected(true);
        } else {
            lightTheme.setSelected(true);
        }

        setTitle(TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);

        setMinimumSize(new Dimension(800, 600));

        // Add components to the frame
        add(mainPanel, BorderLayout.CENTER);
    }

    // ... rest of the MainFrame class ...

    public void registerScreen(String name, JPanel screen) {
        screens.put(name, screen);
        mainPanel.add(screen, name);
    }

    public JPanel getScreen(String screenName) {
        return screens.get(screenName);
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

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu(FILE);
        JMenuItem languageMenuItem = new JMenuItem(LANGUAGE);
        languageMenuItem.addActionListener(e -> new ChooseLanguageDialog(this).setVisible(true));
        fileMenu.add(languageMenuItem);
        JMenuItem exitMenuItem = new JMenuItem(EXIT);
        exitMenuItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitMenuItem);

        // Theme Menu
        JMenu themeMenu = new JMenu(THEME);
        lightTheme = new JCheckBoxMenuItem(LIGHT);
        lightTheme.addActionListener(this::lightThemeClicked);
        themeMenu.add(lightTheme);

        darkTheme = new JCheckBoxMenuItem(DARK);
        darkTheme.addActionListener(this::darkThemeClicked);
        themeMenu.add(darkTheme);

        // Help Menu
        JMenu helpMenu = new JMenu(HELP);
        JMenuItem aboutMenuItem = new JMenuItem(ABOUT);
        aboutMenuItem.addActionListener(e -> new AboutDialog(this).setVisible(true));
        helpMenu.add(aboutMenuItem);

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
        Preferences preferences = Preferences.userNodeForPackage(MainFrame.class);
        preferences.put("theme", theme);
    }

    private String getThemePreference() {
        Preferences preferences = Preferences.userNodeForPackage(MainFrame.class);
        return preferences.get("theme", AppConfig.DEFAULT_LIGHT_THEME);
    }
}
