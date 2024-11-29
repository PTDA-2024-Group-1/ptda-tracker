package com.ptda.tracker.ui.renderers;

import com.ptda.tracker.models.assistance.Ticket;

import javax.swing.*;
import java.awt.*;

public class TicketListRenderer extends JPanel implements ListCellRenderer<Ticket> {

    private JLabel titleLabel;
    private JLabel statusLabel;

    private static final String
            UNTITLED_TICKET = "Untitled Ticket",
            STATUS_CLOSED = "Status: Closed",
            STATUS_OPEN = "Status: Open";

    public TicketListRenderer() {
        setLayout(new BorderLayout(10, 10));

        // Labels for rendering
        titleLabel = new JLabel();
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));

        statusLabel = new JLabel();
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        // Left panel for title and status
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(5)); // Vertical space
        leftPanel.add(statusLabel);

        add(leftPanel, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends Ticket> list,
            Ticket ticket,
            int index,
            boolean isSelected,
            boolean cellHasFocus
    ) {
        // Set text values
        titleLabel.setText(ticket.getTitle() != null ? ticket.getTitle() : UNTITLED_TICKET);
        statusLabel.setText(ticket.isClosed() ? STATUS_CLOSED : STATUS_OPEN);
        statusLabel.setForeground(ticket.isClosed() ? Color.RED : Color.GREEN);

        return this;
    }
}