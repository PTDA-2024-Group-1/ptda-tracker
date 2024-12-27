package com.ptda.tracker;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.util.SystemInfo;
import com.ptda.tracker.config.AppConfig;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.user.UserService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.forms.LoginForm;
import com.ptda.tracker.ui.user.screens.CustomSplashScreen;
import com.ptda.tracker.ui.user.screens.NavigationScreen;
import com.ptda.tracker.util.LocaleManager;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InaccessibleObjectException;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.prefs.Preferences;

@SpringBootApplication
public class TrackerApplication {

    public static void main(String[] args) {
        Preferences preferences = Preferences.userNodeForPackage(TrackerApplication.class);
        String language = preferences.get("language", "en");
        String country = preferences.get("country", "US");
        Locale locale = new Locale(language, country);
        LocaleManager.getInstance().setLocale(locale);

        try {
            setLookAndFeel();
        } catch (UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }
        CustomSplashScreen splashScreen = new CustomSplashScreen();
        splashScreen.showSplashScreen();

        // Initialize Spring Application
        ApplicationContext context = SpringApplication.run(TrackerApplication.class, args);

        String username = preferences.get("email", null);
        String encryptedPassword = preferences.get("password", null);
        UserService userService = context.getBean(UserService.class);
        Optional<User> user = userService.getByEmail(username);

        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(context);
            if (user.isPresent() && Objects.equals(encryptedPassword, user.get().getPassword())) {
                UserSession.getInstance().setUser(user.get());
                splashScreen.hideSplashScreen();
                mainFrame.registerAndShowScreen(ScreenNames.NAVIGATION_SCREEN, new NavigationScreen(mainFrame));
                mainFrame.setVisible(true);
            } else if (user.isPresent() && !Objects.equals(encryptedPassword, user.get().getPassword())) {
                splashScreen.hideSplashScreen();
                mainFrame.registerAndShowScreen(ScreenNames.LOGIN_FORM, new LoginForm(mainFrame));
                mainFrame.setVisible(true);
                JOptionPane.showMessageDialog(
                        mainFrame,
                        LocaleManager.getInstance().getTranslation("saved_credentials_incorrect"),
                        LocaleManager.getInstance().getTranslation("error"),
                        JOptionPane.ERROR_MESSAGE
                );
            } else {
                splashScreen.hideSplashScreen();
                mainFrame.registerAndShowScreen(ScreenNames.LOGIN_FORM, new LoginForm(mainFrame));
                mainFrame.setVisible(true);
            }
        });
    }

    private static void setLookAndFeel() throws UnsupportedLookAndFeelException {
        if (SystemInfo.isMacOS) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("apple.awt.application.name", AppConfig.APP_NAME);
            System.setProperty("apple.awt.application.appearance", "system");
        } else if (SystemInfo.isLinux) {
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);

            try {
                var toolkit = Toolkit.getDefaultToolkit();
                var awtAppClassNameField = toolkit.getClass().getDeclaredField("awtAppClassName");
                awtAppClassNameField.setAccessible(true);
                awtAppClassNameField.set(toolkit, AppConfig.APP_NAME);
            } catch (NoSuchFieldException | InaccessibleObjectException | IllegalAccessException e) {
                // LOGGER.debug("Failed to set proper app name");
            }
        }

        UIManager.setLookAndFeel(new FlatLightLaf());

        FlatLaf.registerCustomDefaultsSource("com.ptda.tracker.theme.custom");

        final int rounding = 8;
        final int insets = 2;

        UIManager.put("CheckBox.icon.style", "filled");
        UIManager.put("Component.arrowType", "chevron");

        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.innerFocusWidth", 1);

        UIManager.put("Button.arc", rounding);
        UIManager.put("Component.arc", rounding);
        UIManager.put("ProgressBar.arc", rounding);
        UIManager.put("TextComponent.arc", rounding);

        UIManager.put("ScrollBar.thumbArc", rounding);
        UIManager.put("ScrollBar.thumbInsets", new Insets(insets, insets, insets, insets));

        // Set default font
        Font defaultFont = new Font("Arial", Font.PLAIN, 14);
        UIManager.put("defaultFont", defaultFont);

        System.setProperty("java.awt.headless", "false");
    }
}
