package com.ptda.tracker.ui.renderers;

import com.ptda.tracker.models.tracker.Expense;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExpenseListRenderer extends JPanel implements ListCellRenderer<Expense> {

    private JLabel amountLabel;
    private JLabel titleLabel;
    private JLabel categoryLabel;
    private JLabel dateLabel;

    public ExpenseListRenderer() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        setBackground(Color.WHITE);

        // Labels for rendering
        amountLabel = new JLabel();
        amountLabel.setFont(new Font("Arial", Font.BOLD, 14));

        titleLabel = new JLabel();
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLabel.setForeground(Color.DARK_GRAY);

        categoryLabel = new JLabel();
        categoryLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        categoryLabel.setForeground(Color.GRAY);

        dateLabel = new JLabel();
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dateLabel.setForeground(Color.GRAY);

        // Left panel for main content
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.add(amountLabel);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(titleLabel);

        // Right panel for category and date
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);
        rightPanel.add(categoryLabel);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(dateLabel);

        add(leftPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends Expense> list,
            Expense expense,
            int index,
            boolean isSelected,
            boolean cellHasFocus
    ) {
        // Format and set values
        amountLabel.setText(String.format("€%.2f", expense.getAmount()));
        titleLabel.setText(expense.getTitle() != null ? expense.getTitle() : "No Title");
        categoryLabel.setText(expense.getCategory() != null ? expense.getCategory().toString() : "OTHER");

        // Format date
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        dateLabel.setText(expense.getDate() != null ? sdf.format(expense.getDate()) : sdf.format(new Date()));

        // Highlighting for selection
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(Color.WHITE);
            setForeground(Color.BLACK);
        }

        return this;
    }
}
