package com.ptda.tracker.ui.user.screens;

import com.ptda.tracker.models.assistance.Ticket;
import com.ptda.tracker.services.assistance.TicketService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.forms.TicketForm;
import com.ptda.tracker.ui.user.components.renderers.TicketListRenderer;
import com.ptda.tracker.ui.user.views.TicketDetailView;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class UserTicketsScreen extends JPanel {
    private final TicketService ticketService;
    private final JList<Ticket> ticketList;
    private List<Ticket> tickets;

    public UserTicketsScreen(MainFrame mainFrame) {
        setLayout(new BorderLayout());

        ticketList = new JList<>(new DefaultListModel<>());
        ticketList.setCellRenderer(new TicketListRenderer());
        ticketService = mainFrame.getContext().getBean(TicketService.class);
        tickets = ticketService.getAllByUserId(UserSession.getInstance().getUser().getId());
        setTicketList(tickets);

        ticketList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Ticket selectedTicket = ticketList.getSelectedValue();
                if (selectedTicket != null) {
                    mainFrame.registerAndShowScreen(ScreenNames.TICKET_DETAIL_VIEW, new TicketDetailView(mainFrame, selectedTicket, ScreenNames.USER_TICKETS_SCREEN));
                    ticketList.clearSelection(); // Clear selection to allow new interaction
                }
            }
        });

        add(new JScrollPane(ticketList), BorderLayout.CENTER);

        JLabel label = new JLabel(SELECT_TICKET, SwingConstants.CENTER);
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
        tickets = ticketService.getAllByUserId(UserSession.getInstance().getUser().getId());
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