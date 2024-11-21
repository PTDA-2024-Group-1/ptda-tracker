package com.ptda.tracker;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.user.UserService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.forms.LoginForm;
import com.ptda.tracker.util.ScreenNames;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;
import java.util.prefs.Preferences;

@SpringBootApplication
public class TrackerApplication {

	public static void main(String[] args) throws UnsupportedLookAndFeelException {
		// Set FlatLaf look and feel
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

		System.setProperty("java.awt.headless", "false");
		ApplicationContext context = SpringApplication.run(TrackerApplication.class, args);

		SwingUtilities.invokeLater(() -> {
			MainFrame mainFrame = new MainFrame(context);

			Preferences preferences = Preferences.userNodeForPackage(TrackerApplication.class);
			String username = preferences.get("email", null);
			String password = preferences.get("password", null);

			UserService userService = context.getBean(UserService.class);
			Optional<User> user = userService.login(username, password);
			if (user.isPresent()) {
				LoginForm.onAuthSuccess(user.get(), mainFrame);
			} else {
				mainFrame.registerScreen(ScreenNames.LOGIN_FORM, new LoginForm(mainFrame));
				mainFrame.showScreen(ScreenNames.LOGIN_FORM);
			}
			mainFrame.setVisible(true);
		});
	}
}
