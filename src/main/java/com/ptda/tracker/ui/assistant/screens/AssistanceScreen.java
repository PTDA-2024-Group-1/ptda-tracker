package com.ptda.tracker.ui.assistant.screens;

import com.ptda.tracker.models.assistance.Ticket;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.assistance.TicketService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.components.renderers.TicketListRenderer;
import com.ptda.tracker.ui.user.views.TicketDetailView;
import com.ptda.tracker.util.Refreshable;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class AssistanceScreen extends JPanel implements Refreshable {
    private static TicketService ticketService;
    private static JList<Ticket> assignedTicketList;
    private static JList<Ticket> unassignedTicketList;
    private static List<Ticket> assignedTickets;
    private static List<Ticket> unassignedTickets;
    private final JComboBox<String> ticketSelector;
    private final JPanel listPanel;

    public AssistanceScreen(MainFrame mainFrame) {
        setLayout(new BorderLayout());

        ticketService = mainFrame.getContext().getBean(TicketService.class);

        ticketSelector = new JComboBox<>(new String[]{"Assigned Ticket", "Tickets to be Assigned"});
        ticketSelector.addActionListener(e -> updateTicketListVisibility());
        add(ticketSelector, BorderLayout.NORTH);

        assignedTicketList = new JList<>(new DefaultListModel<>());
        assignedTicketList.setCellRenderer(new TicketListRenderer());
        assignedTicketList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Ticket selectedTicket = assignedTicketList.getSelectedValue();
                if (selectedTicket != null) {
                    mainFrame.registerAndShowScreen(ScreenNames.TICKET_DETAIL_VIEW, new TicketDetailView(mainFrame, selectedTicket, ScreenNames.ASSISTANCE_SCREEN));
                    assignedTicketList.clearSelection(); // Clear selection to allow new interaction
                }
            }
        });

        unassignedTicketList = new JList<>(new DefaultListModel<>());
        unassignedTicketList.setCellRenderer(new TicketListRenderer());
        unassignedTicketList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Ticket selectedTicket = unassignedTicketList.getSelectedValue();
                if (selectedTicket != null) {
                    mainFrame.registerAndShowScreen(ScreenNames.TICKET_DETAIL_VIEW, new TicketDetailView(mainFrame, selectedTicket, ScreenNames.ASSISTANCE_SCREEN));
                    unassignedTicketList.clearSelection(); // Clear selection to allow new interaction
                }
            }
        });

        listPanel = new JPanel(new CardLayout());
        listPanel.add(new JScrollPane(assignedTicketList), "Assigned Ticket");
        listPanel.add(new JScrollPane(unassignedTicketList), "Tickets to be Assigned");
        add(listPanel, BorderLayout.CENTER);

        refreshTicketLists();
        updateTicketListVisibility();
    }

    public static void refreshTicketLists() {
        User currentUser = UserSession.getInstance().getUser();
        List<Ticket> allTickets = ticketService.getAll();
        assignedTickets = allTickets.stream()
                .filter(ticket -> ticket.getAssistant() != null && ticket.getAssistant().getId().equals(currentUser.getId()) && !ticket.isClosed())
                .collect(Collectors.toList());
        unassignedTickets = allTickets.stream()
                .filter(ticket -> ticket.getAssistant() == null && !ticket.isClosed() && !ticket.getCreatedBy().getId().equals(currentUser.getId()))
                .collect(Collectors.toList());
        setTicketList(assignedTicketList, assignedTickets);
        setTicketList(unassignedTicketList, unassignedTickets);
    }

    private static void setTicketList(JList<Ticket> list, List<Ticket> tickets) {
        DefaultListModel<Ticket> model = (DefaultListModel<Ticket>) list.getModel();
        model.clear(); // Clear old data
        tickets.forEach(model::addElement); // Add new data
    }

    private void updateTicketListVisibility() {
        CardLayout cl = (CardLayout) (listPanel.getLayout());
        String selectedOption = (String) ticketSelector.getSelectedItem();
        cl.show(listPanel, selectedOption);
    }

    @Override
    public void refresh() {
        refreshTicketLists();
    }
}