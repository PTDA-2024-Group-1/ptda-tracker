package com.ptda.tracker.ui.forms;

import com.ptda.tracker.TrackerApplication;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.user.UserService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.screens.NavigationScreen;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;
import java.util.prefs.Preferences;

public class LoginForm extends JPanel {
    private MainFrame mainFrame;
    private UserService userService;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private static final String HOME_SCREEN = ScreenNames.HOME_SCREEN;
    private static final String BUDGETS_SCREEN = ScreenNames.BUDGETS_SCREEN;
    private static final String PROFILE_SCREEN = ScreenNames.PROFILE_SCREEN;
    private static final String NAVIGATION_SCREEN = ScreenNames.NAVIGATION_SCREEN;
    private static final String REGISTER_SCREEN = ScreenNames.REGISTER_FORM;
    private static final String LOGIN_SCREEN = ScreenNames.LOGIN_FORM;

    public LoginForm(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        ApplicationContext context = mainFrame.getContext();
        userService = context.getBean(UserService.class);

        setLayout(new GridLayout(5, 1, 10, 10)); // 5 rows, 1 column, 10px horizontal and vertical gaps

        JLabel label = new JLabel("Login Form", SwingConstants.CENTER);
        add(label);

        usernameField = new JTextField();
        add(usernameField);

        passwordField = new JPasswordField();
        add(passwordField);

        loginButton = new JButton("Login");
        loginButton.addActionListener(e -> login());
        add(loginButton);

        JButton button = new JButton("Go to Register");
        button.addActionListener(e -> {
            mainFrame.registerScreen(ScreenNames.REGISTER_FORM, new RegisterForm(mainFrame));
            mainFrame.showScreen(ScreenNames.REGISTER_FORM);
        });
        add(button);
    }

    private void login() {
        String email = usernameField.getText();
        String password = new String(passwordField.getPassword());
        Optional<User> user = userService.login(email, password);
        if (user.isPresent()) {
            onAuthSuccess(user.get(), mainFrame);
            JOptionPane.showMessageDialog(this, "Login successful", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Login failed", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void onAuthSuccess(User user, MainFrame mainFrame) {
        Preferences preferences = Preferences.userNodeForPackage(TrackerApplication.class);
        preferences.put("email", user.getEmail());
        preferences.put("password", user.getPassword());
        UserSession.getInstance().setUser(user);

        mainFrame.removeScreen(REGISTER_SCREEN);
        mainFrame.removeScreen(LOGIN_SCREEN);

        // Create the NavigationScreen
        NavigationScreen navigationScreen = new NavigationScreen(mainFrame);
        mainFrame.registerAndShowScreen(ScreenNames.NAVIGATION_SCREEN, navigationScreen);
        mainFrame.setVisible(true);
    }

}
