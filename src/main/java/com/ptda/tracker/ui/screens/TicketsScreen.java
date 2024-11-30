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
                    ticketList.clearSelection(); // Clear selection to allow new interaction
                }
            }
        });

        add(new JScrollPane(ticketList), BorderLayout.CENTER);

        JLabel label = new JLabel(SELECT_TICKET, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        add(label, BorderLayout.NORTH);

        JButton createButton = new JButton(CREATE_NEW_TICKET);
        createButton.addActionListener(e -> {
            // Open TicketForm in creation mode
            mainFrame.registerScreen(ScreenNames.TICKET_FORM, new TicketForm(mainFrame, this::refreshTicketList, null));
            mainFrame.showScreen(ScreenNames.TICKET_FORM);
        });
        add(createButton, BorderLayout.SOUTH);
    }

    private void refreshTicketList() {
        ticketList.clearSelection();
        tickets = ticketService.getAllByUser(UserSession.getInstance().getUser());
        setTicketList(tickets);
    }

    public void setTicketList(List<Ticket> tickets) {
        DefaultListModel<Ticket> model = (DefaultListModel<Ticket>) ticketList.getModel();
        model.clear(); // Clear old data
        tickets.forEach(model::addElement); // Add new data
    }

    private static final String
            SELECT_TICKET = "Select a ticket to view details",
            CREATE_NEW_TICKET = "Create New Ticket";
}