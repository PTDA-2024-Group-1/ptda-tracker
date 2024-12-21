package com.ptda.tracker.ui.admin.views;

import com.ptda.tracker.models.assistance.Ticket;
import com.ptda.tracker.services.assistance.TicketService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.admin.screens.AdministrationOptionsScreen;
import com.ptda.tracker.ui.user.components.renderers.TicketListRenderer;
import com.ptda.tracker.ui.user.views.TicketDetailView;
import com.ptda.tracker.util.ScreenNames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ManageTicketView extends JPanel {
    private final TicketService ticketService;
    private final JList<Ticket> ticketList;
    private List<Ticket> tickets;

    public ManageTicketView(MainFrame mainFrame) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("Manage Tickets", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Ticket List
        ticketList = new JList<>(new DefaultListModel<>());
        ticketList.setCellRenderer(new TicketListRenderer());
        ticketService = mainFrame.getContext().getBean(TicketService.class);
        tickets = ticketService.getAll();
        setTicketList(tickets);

        JScrollPane scrollPane = new JScrollPane(ticketList);
        add(scrollPane, BorderLayout.CENTER);

        // Ticket Selection Listener (using MouseAdapter)
        ticketList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Ticket selectedTicket = ticketList.getSelectedValue();
                    if (selectedTicket != null) {
                        mainFrame.registerAndShowScreen(ScreenNames.TICKET_DETAIL_VIEW, new TicketDetailView(mainFrame, selectedTicket));
                        ticketList.clearSelection(); // Clear selection after click
                    }
                }
            }
        });

        // Back Button Panel
        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton(BACK);
        backButton.addActionListener(e -> {
            AdministrationOptionsScreen administrationOptionsScreen = new AdministrationOptionsScreen(mainFrame);
            administrationOptionsScreen.refreshData();
            mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN);
        });
        leftButtonPanel.add(backButton);
        add(leftButtonPanel, BorderLayout.SOUTH);
    }

    public void setTicketList(List<Ticket> tickets) {
        DefaultListModel<Ticket> model = (DefaultListModel<Ticket>) ticketList.getModel();
        model.clear(); // Clear old data
        tickets.forEach(model::addElement); // Add new data
    }

    private static final String
            BACK = "Back",
            SELECT_TICKET = "Select a ticket to view details";
}