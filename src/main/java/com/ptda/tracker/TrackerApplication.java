package com.ptda.tracker;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.BudgetAccess;
import com.ptda.tracker.models.tracker.BudgetAccessLevel;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.services.tracker.BudgetAccessService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;

@SpringBootApplication
public class TrackerApplication {

	public static void main(String[] args) throws UnsupportedLookAndFeelException {

		// Set FlatLaf look and feel
		UIManager.setLookAndFeel(new FlatLightLaf());

		FlatLaf.registerCustomDefaultsSource("com.example.theme.custom");

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

		ConfigurableApplicationContext context = new SpringApplicationBuilder(TrackerApplication.class).headless(false).run(args);

		SpringApplication.run(TrackerApplication.class, args);

		SwingUtilities.invokeLater(() -> {
//			TrackerApplication app = new TrackerApplication();
//			app.setVisible(true);
		});
	}

}
