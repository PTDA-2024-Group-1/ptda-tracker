package com.ptda.tracker.theme;

import com.formdev.flatlaf.FlatLaf;
import com.jthemedetecor.OsThemeDetector;
import com.ptda.tracker.config.AppConfig;
import com.ptda.tracker.theme.custom.Dark;
import com.ptda.tracker.theme.custom.Light;
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
        this.window = window;

        // Custom themes (in resources folder)
        // https://www.formdev.com/flatlaf/theme-editor/
        Light.installLafInfo();
        Dark.installLafInfo();

        // Install other themes
        // FlatGitHubDarkContrastIJTheme.installLafInfo();

        final OsThemeDetector detector = OsThemeDetector.getDetector();
        var selectedTheme = detector.isDark() ? AppConfig.DEFAULT_DARK_THEME : AppConfig.DEFAULT_LIGHT_THEME;
        setTheme(selectedTheme);
    }

    public void setTheme(String theme) {
        try {
            var themeInfo = Arrays.stream(UIManager.getInstalledLookAndFeels())
                    .filter(x -> x.getName().equals(theme))
                    .findFirst()
                    .orElseThrow();

            var clazz = (Class<FlatLaf>) Class.forName(themeInfo.getClassName());
            var instance = clazz.getDeclaredConstructor().newInstance();

            isDark = instance.isDark();

            FlatLaf.setup(instance);
            FlatLaf.updateUI();
        } catch (NoSuchElementException | NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                 ClassNotFoundException | InstantiationException e) {
            LOGGER.error("Unable to set FlatLaf theme", e);
            setSystemLookAndFeel();
        }
    }

    public void toggleTheme() {
        if (isDark) {
            setTheme(AppConfig.DEFAULT_LIGHT_THEME);
        } else {
            setTheme(AppConfig.DEFAULT_DARK_THEME);
        }
        isDark = !isDark;
    }

    private void setSystemLookAndFeel() {
        try {
            JFrame.setDefaultLookAndFeelDecorated(false);
            JDialog.setDefaultLookAndFeelDecorated(false);
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(window);
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
                 IllegalAccessException e) {
            LOGGER.error("Unable to set system look and feel", e);
        }
    }
}
