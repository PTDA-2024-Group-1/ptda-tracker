package com.ptda.tracker.ui.renderers;

import com.ptda.tracker.models.assistance.Ticket;

import javax.swing.*;
import java.awt.*;

public class TicketListRenderer extends JPanel implements ListCellRenderer<Ticket> {

    private JLabel titleLabel;
    private JLabel statusLabel;

    public TicketListRenderer() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        setBackground(Color.WHITE);

        // Labels para renderização
        titleLabel = new JLabel();
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));

        statusLabel = new JLabel();
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        // Painel esquerdo para título e estado
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(5)); // Espaço vertical
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
        // Define os valores dos textos
        titleLabel.setText(ticket.getTitle() != null ? ticket.getTitle() : "Untitled Ticket");
        statusLabel.setText(ticket.isClosed() ? "Status: Closed" : "Status: Open");
        statusLabel.setForeground(ticket.isClosed() ? Color.RED : Color.GREEN);

        // Destaque para seleção
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