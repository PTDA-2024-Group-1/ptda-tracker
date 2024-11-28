package com.ptda.tracker.ui.renderers;

import com.ptda.tracker.models.tracker.Budget;

import javax.swing.*;
import java.awt.*;

public class BudgetListRenderer extends JPanel implements ListCellRenderer<Budget> {

    private JLabel nameLabel;
    private JLabel descriptionLabel;

    public BudgetListRenderer() {
        setLayout(new BorderLayout(10, 10));
//        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
//        setBackground(Color.WHITE);

        // Labels for rendering
        nameLabel = new JLabel();
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        descriptionLabel = new JLabel();
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
//        descriptionLabel.setForeground(Color.DARK_GRAY);

        // Left panel for name and description
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.add(nameLabel);
        leftPanel.add(Box.createVerticalStrut(5)); // Spacer
        leftPanel.add(descriptionLabel);

        add(leftPanel, BorderLayout.CENTER);
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
        nameLabel.setText(budget.getName() != null ? budget.getName() : "Unnamed Budget");
        descriptionLabel.setText(budget.getDescription() != null ? budget.getDescription() : "No Description");

        // Highlighting for selection
//        if (isSelected) {
//            setBackground(list.getSelectionBackground());
//            setForeground(list.getSelectionForeground());
//        } else {
//            setBackground(Color.WHITE);
//            setForeground(Color.BLACK);
//        }

        return this;
    }
}
