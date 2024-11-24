package com.ptda.tracker.ui.screens;

import com.ptda.tracker.models.assistance.Ticket;
import com.ptda.tracker.services.tracker.TicketService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.forms.TicketForm;
import com.ptda.tracker.ui.renderers.TicketListRenderer;
import com.ptda.tracker.ui.views.TicketDetailView;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TicketsScreen extends JPanel {
    private final TicketService ticketService;
    private final JList<Ticket> ticketList;
    private List<Ticket> tickets;

    public TicketsScreen(MainFrame mainFrame) {
        setLayout(new BorderLayout());

        ticketList = new JList<>(new DefaultListModel<>());
        ticketList.setCellRenderer(new TicketListRenderer());
        ticketService = mainFrame.getContext().getBean(TicketService.class);
        tickets = ticketService.getAllByUser(UserSession.getInstance().getUser());
        setTicketList(tickets);

        ticketList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Ticket selectedTicket = ticketList.getSelectedValue();
                if (selectedTicket != null) {
                    mainFrame.registerAndShowScreen(ScreenNames.TICKET_DETAIL_VIEW, new TicketDetailView(mainFrame, selectedTicket));
                    ticketList.clearSelection(); // Limpar seleção para permitir nova interação
                }
            }
        });

        add(new JScrollPane(ticketList), BorderLayout.CENTER);

        JLabel label = new JLabel("Select a ticket to view details", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        add(label, BorderLayout.NORTH);

        JButton createButton = new JButton("Create New Ticket");
        styleButton(createButton);
        createButton.addActionListener(e -> {
            // Abrir o TicketForm no modo de criação
            mainFrame.registerScreen(ScreenNames.TICKET_FORM, new TicketForm(mainFrame, this::refreshTicketList, null));
            mainFrame.showScreen(ScreenNames.TICKET_FORM);
        });
        add(createButton, BorderLayout.SOUTH);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(56, 56, 56));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 40));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Efeito de hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 0, 0)); // Cor mais escura ao passar o mouse
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(56, 56, 56)); // Voltar à cor original
            }
        });
    }

    private void refreshTicketList() {
        ticketList.clearSelection();
        tickets = ticketService.getAllByUser(UserSession.getInstance().getUser());
        setTicketList(tickets);
    }

    public void setTicketList(List<Ticket> tickets) {
        DefaultListModel<Ticket> model = (DefaultListModel<Ticket>) ticketList.getModel();
        model.clear(); // Limpar dados antigos
        tickets.forEach(model::addElement); // Adicionar novos dados
    }
}
