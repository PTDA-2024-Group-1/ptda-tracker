package com.ptda.tracker.theme;

import com.formdev.flatlaf.FlatLaf;
import com.jthemedetecor.OsThemeDetector;
import com.ptda.tracker.config.AppConfig;
import com.ptda.tracker.theme.custom.Dark;
import com.ptda.tracker.theme.custom.Light;
import com.ptda.tracker.ui.MainFrame;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.NoSuchElementException;

public class ThemeManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThemeManager.class);

    @Getter
    private boolean isDark;
    private final Component window;

    public ThemeManager(Component window) {
        LOGGER.debug("Initializing ThemeManager...");
        this.window = window;

        // Register custom themes
        Light.installLafInfo();
        Dark.installLafInfo();

        // Detect OS theme as the default preference
        final OsThemeDetector detector = OsThemeDetector.getDetector();
        String selectedTheme = detector.isDark() ? AppConfig.DEFAULT_DARK_THEME : AppConfig.DEFAULT_LIGHT_THEME;

        LOGGER.debug("Detected OS theme preference: {}", detector.isDark() ? "Dark" : "Light");

        setTheme(selectedTheme);
        LOGGER.debug("ThemeManager initialized successfully.");
    }

    /**
     * Sets the theme using FlatLaf or falls back to system look and feel on failure.
     */
    public void setTheme(String theme) {
        LOGGER.debug("Applying theme: {}", theme);
        try {
            var themeInfo = Arrays.stream(UIManager.getInstalledLookAndFeels())
                    .filter(x -> x.getName().equals(theme))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Theme not found: " + theme));

            // Use reflection to initialize FlatLaf theme
            var clazz = (Class<FlatLaf>) Class.forName(themeInfo.getClassName());
            var instance = clazz.getDeclaredConstructor().newInstance();

            isDark = instance.isDark();
            LOGGER.debug("Theme '{}' applied successfully. isDark={}", theme, isDark);

            FlatLaf.setup(instance);
            FlatLaf.updateUI();
        } catch (NoSuchElementException | NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException | ClassNotFoundException | InstantiationException e) {
            LOGGER.error("Error applying theme '{}'. Falling back to system look and feel.", theme, e);
            setSystemLookAndFeel();
        }

        notifyThemeChange();
    }

    /**
     * Toggles between light and dark themes.
     */
    public void toggleTheme() {
        LOGGER.debug("Toggling theme. Current isDark={}", isDark);
        if (isDark) {
            setTheme(AppConfig.DEFAULT_LIGHT_THEME);
        } else {
            setTheme(AppConfig.DEFAULT_DARK_THEME);
        }
        isDark = !isDark;
        LOGGER.debug("Theme toggled successfully. New isDark={}", isDark);
    }

    /**
     * Notifies the window (MainFrame) to update components, such as the logo.
     */
    private void notifyThemeChange() {
        LOGGER.debug("Notifying theme change...");
        if (window instanceof MainFrame) {
            SwingUtilities.updateComponentTreeUI(window);
        } else {
            LOGGER.warn("Window is not an instance of MainFrame. Cannot update logo.");
        }
    }

    /**
     * Falls back to the system's look and feel if the custom theme setup fails.
     */
    private void setSystemLookAndFeel() {
        LOGGER.debug("Applying system look and feel as fallback.");
        try {
            JFrame.setDefaultLookAndFeelDecorated(false);
            JDialog.setDefaultLookAndFeelDecorated(false);
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(window);
            LOGGER.debug("System look and feel applied successfully.");
        } catch (Exception e) {
            LOGGER.error("Failed to apply system look and feel.", e);
        }
    }
}
