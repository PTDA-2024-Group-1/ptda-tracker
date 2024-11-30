package com.ptda.tracker.ui.user.views;

import com.ptda.tracker.models.assistance.Ticket;
import com.ptda.tracker.models.assistance.TicketReply;
import com.ptda.tracker.services.tracker.TicketService;
import com.ptda.tracker.services.tracker.TicketReplyService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.forms.TicketReplyForm;
import com.ptda.tracker.ui.user.renderers.TicketReplyRenderer;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;
import org.jdesktop.swingx.JXList;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TicketDetailView extends JPanel {
    private final MainFrame mainFrame;
    private final Ticket ticket;
    private final TicketService ticketService;
    private final TicketReplyService ticketReplyService;
    private List<TicketReply> replies;

    public TicketDetailView(MainFrame mainFrame, Ticket ticket) {
        this.mainFrame = mainFrame;
        this.ticket = ticket;
        this.ticketService = mainFrame.getContext().getBean(TicketService.class);
        this.ticketReplyService = mainFrame.getContext().getBean(TicketReplyService.class);

        initComponents();
    }

    private void refreshRepliesList() {
        replies = ticketReplyService.getAllByTicketId(ticket.getId());
        DefaultListModel<TicketReply> model = (DefaultListModel<TicketReply>) repliesList.getModel();
        model.clear();
        replies.forEach(model::addElement);
    }

    private void addButtons(JPanel buttonPanel) {
        JButton backButton = new JButton(BACK);
        backButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN));
        buttonPanel.add(backButton);

        if (ticket.isClosed()) {
            JButton reopenButton = new JButton(REOPEN_TICKET);
            reopenButton.addActionListener(e -> {
                TicketReply reply = TicketReply.builder()
                        .ticket(ticket)
                        .createdBy(UserSession.getInstance().getUser())
                        .body("Reopened the ticket.")
                        .build();
                ticketReplyService.save(reply);
                ticket.setClosed(false);
                ticketService.update(ticket);
                JOptionPane.showMessageDialog(this, TICKET_REOPENED_SUCCESS);
                refreshRepliesList();
                updateButtons(buttonPanel);
            });
            buttonPanel.add(reopenButton);
        } else {
            JButton closeButton = new JButton(CLOSE_TICKET);
            closeButton.addActionListener(e -> {
                ticket.setClosed(true);
                ticketService.update(ticket);
                JOptionPane.showMessageDialog(this, TICKET_CLOSED_SUCCESS);
                refreshRepliesList();
                updateButtons(buttonPanel);
            });
            buttonPanel.add(closeButton);

            JButton replyButton = new JButton(REPLY);
            replyButton.addActionListener(e -> mainFrame.registerAndShowScreen(ScreenNames.TICKET_REPLY_FORM, new TicketReplyForm(mainFrame, null, ticket)));
            buttonPanel.add(replyButton);
        }
    }

    private void updateButtons(JPanel buttonPanel) {
        buttonPanel.removeAll();
        addButtons(buttonPanel);
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel(ticket.getTitle(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Description
        JTextArea descriptionArea = new JTextArea(ticket.getBody());
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setBorder(BorderFactory.createTitledBorder(TICKET_DESCRIPTION));
        mainPanel.add(descriptionScroll, BorderLayout.NORTH);

        // Replies List
        repliesList = new JXList(new DefaultListModel<>());
        repliesList.setCellRenderer(new TicketReplyRenderer());
        refreshRepliesList();
        JScrollPane repliesScroll = new JScrollPane(repliesList);
        repliesScroll.setBorder(BorderFactory.createTitledBorder(REPLIES));
        repliesScroll.setPreferredSize(new Dimension(300, 200));
        mainPanel.add(repliesScroll, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Action Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addButtons(buttonPanel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JXList repliesList;
    private static final String
            TICKET_DESCRIPTION = "Ticket Description",
            REPLIES = "Replies",
            BACK = "Back",
            REOPEN_TICKET = "Reopen Ticket",
            REPLY = "Reply",
            CLOSE_TICKET = "Close Ticket",
            TICKET_REOPENED_SUCCESS = "Ticket reopened successfully.",
            TICKET_CLOSED_SUCCESS = "Ticket closed successfully.";
}