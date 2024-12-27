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
    private final MainFrame mainFrame;
    private final String returnScreen;
    private TicketService ticketService;

    public ManageTicketView(MainFrame mainFrame, String returnScreen) {
        this.mainFrame = mainFrame;
        this.returnScreen = returnScreen;


        initComponents();
    }

    public void setTicketList(List<Ticket> tickets) {
        DefaultListModel<Ticket> model = (DefaultListModel<Ticket>) ticketList.getModel();
        model.clear(); // Clear old data
        tickets.forEach(model::addElement); // Add new data
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel(MANAGE_TICKETS, SwingConstants.LEFT);
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
                        mainFrame.registerAndShowScreen(ScreenNames.TICKET_DETAIL_VIEW, new TicketDetailView(mainFrame, selectedTicket, ScreenNames.MANAGE_TICKET_VIEW));
                        ticketList.clearSelection(); // Clear selection after click
                    }
                }
            }
        });

        // Back Button Panel
        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton(BACK);
        backButton.addActionListener(e -> {
            mainFrame.showScreen(returnScreen);
        });
        leftButtonPanel.add(backButton);
        add(leftButtonPanel, BorderLayout.SOUTH);
    }

    private JList<Ticket> ticketList;
    private List<Ticket> tickets;
    private static final String
            BACK = "Back",
            MANAGE_TICKETS = "Manage Tickets";
}