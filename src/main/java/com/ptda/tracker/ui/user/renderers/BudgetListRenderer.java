package com.ptda.tracker.ui.user.renderers;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.util.LocaleManager;

import javax.swing.*;
import java.awt.*;

import static com.ptda.tracker.config.AppConfig.FAVORITE_ICON_PATH;

public class BudgetListRenderer extends JPanel implements ListCellRenderer<Budget> {

    public BudgetListRenderer() {
        setLayout(new BorderLayout(10, 10));

        // Labels for rendering
        nameLabel = new JLabel();
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        descriptionLabel = new JLabel();
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        starLabel = new JLabel();
        java.net.URL imgURL = getClass().getResource(FAVORITE_ICON_PATH);
        if (imgURL != null) {
            ImageIcon originalIcon = new ImageIcon(imgURL);
            Image scaledImage = originalIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH); // Resize to 16x16 pixels
            starLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            System.err.println("Couldn't find file: /images/favorite.png");
        }

        // Left panel for name and description
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.add(nameLabel);
        leftPanel.add(Box.createVerticalStrut(5)); // Spacer
        leftPanel.add(descriptionLabel);

        add(leftPanel, BorderLayout.CENTER);
        add(starLabel, BorderLayout.EAST);
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends Budget> list,
            Budget budget,
            int index,
            boolean isSelected,
            boolean cellHasFocus
    ) {
        // Set text values
        nameLabel.setText(budget.getName() != null ? budget.getName() : UNNAMED_BUDGET);
        descriptionLabel.setText(budget.getDescription() != null ? budget.getDescription() : NO_DESCRIPTION);

        // Show or hide the star icon based on the isFavorite property
        starLabel.setVisible(budget.isFavorite());

        return this;
    }

    private JLabel nameLabel;
    private JLabel descriptionLabel;
    private JLabel starLabel;
    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            UNNAMED_BUDGET = localeManager.getTranslation("unnamed_budget"),
            NO_DESCRIPTION = localeManager.getTranslation("no_description");

}