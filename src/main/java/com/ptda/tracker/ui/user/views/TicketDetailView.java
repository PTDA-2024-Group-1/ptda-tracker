package com.ptda.tracker.ui.user.views;

import com.ptda.tracker.models.assistance.Assistant;
import com.ptda.tracker.models.assistance.Ticket;
import com.ptda.tracker.models.assistance.TicketReply;
import com.ptda.tracker.services.assistance.TicketService;
import com.ptda.tracker.services.assistance.TicketReplyService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.admin.dialogs.ChangeAssignmentDialog;
import com.ptda.tracker.ui.admin.views.ManageTicketView;
import com.ptda.tracker.ui.assistant.screens.AssistanceScreen;
import com.ptda.tracker.ui.user.components.renderers.TicketReplyRenderer;
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

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel(ticket.getTitle(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

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

        // Chat Panel
        if (UserSession.getInstance().getUser().getUserType().equals("USER") || UserSession.getInstance().getUser().getUserType().equals("ASSISTANT")) {
            JPanel chatPanel = new JPanel(new BorderLayout());
            chatPanel.setBorder(BorderFactory.createTitledBorder(REPLY));

            // Text area for message input
            JTextArea chatArea = new JTextArea();
            chatArea.setLineWrap(true);
            chatArea.setWrapStyleWord(true);
            chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
            chatArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            // Send button
            JButton sendButton = new JButton("Send");
            sendButton.addActionListener(e -> {
                String replyBody = chatArea.getText().trim();
                if (!replyBody.isEmpty()) {
                    TicketReply reply = TicketReply.builder()
                            .ticket(ticket)
                            .createdBy(UserSession.getInstance().getUser())
                            .body(replyBody)
                            .build();
                    ticketReplyService.create(reply);
                    chatArea.setText("");
                    refreshRepliesList();
                    if (ticket.getAssistant() != null) {
                        AssistanceScreen.refreshTicketLists();
                    }
                }
            });

            // Layout for input and button
            JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
            inputPanel.add(chatArea, BorderLayout.CENTER);
            inputPanel.add(sendButton, BorderLayout.EAST);

            chatPanel.add(inputPanel, BorderLayout.CENTER);
            mainPanel.add(chatPanel, BorderLayout.SOUTH);
        }

        add(mainPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new BorderLayout());
        addButtons(buttonPanel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addButtons(JPanel buttonPanel) {
        buttonPanel.setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton(BACK);
        backButton.addActionListener(e -> {
            if (UserSession.getInstance().getUser().getUserType().equals("ADMIN")) {
                mainFrame.registerAndShowScreen(ScreenNames.MANAGE_TICKET_VIEW, new ManageTicketView(mainFrame));
            } else {
                mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN);
            }
        });
        leftPanel.add(backButton);

        buttonPanel.add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        if (UserSession.getInstance().getUser().getUserType().equals("USER")) {
            if (ticket.isClosed()) {
                reopenButton.addActionListener(e -> {
                    TicketReply reply = TicketReply.builder()
                            .ticket(ticket)
                            .createdBy(UserSession.getInstance().getUser())
                            .body("Reopened the ticket.")
                            .build();
                    ticketReplyService.create(reply);
                    ticket.setClosed(false);
                    ticketService.update(ticket);
                    JOptionPane.showMessageDialog(this, TICKET_REOPENED_SUCCESS);
                    refreshRepliesList();
                    updateButtons(buttonPanel);
                });
                rightPanel.add(reopenButton);
            } else {
                closeButton.addActionListener(e -> {
                    ticket.setClosed(true);
                    ticketService.update(ticket);
                    JOptionPane.showMessageDialog(this, TICKET_CLOSED_SUCCESS);
                    refreshRepliesList();
                    updateButtons(buttonPanel);
                });
                rightPanel.add(closeButton);
            }
        }
        if (UserSession.getInstance().getUser().getUserType().equals("ADMIN")) {
            JButton changeAssignmentButton = new JButton(CHANGE_ASSIGNMENT);
            changeAssignmentButton.addActionListener(e -> {
                ChangeAssignmentDialog dialog = new ChangeAssignmentDialog(mainFrame, ticket);
                dialog.setVisible(true);
            });
            rightPanel.add(changeAssignmentButton);
        }
        buttonPanel.add(rightPanel, BorderLayout.EAST);
    }

    private void updateButtons(JPanel buttonPanel) {
        buttonPanel.removeAll();
        addButtons(buttonPanel);
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private List<TicketReply> replies;
    private JXList repliesList;
    private JButton closeButton = new JButton(CLOSE_TICKET);
    private JButton reopenButton = new JButton(REOPEN_TICKET);

    private static final String
            TICKET_DESCRIPTION = "Ticket Description",
            REPLIES = "Replies",
            REPLY = "Reply",
            BACK = "Back",
            CHANGE_ASSIGNMENT = "Change Assignment",
            REOPEN_TICKET = "Reopen Ticket",
            CLOSE_TICKET = "Close Ticket",
            TICKET_REOPENED_SUCCESS = "Ticket reopened successfully.",
            TICKET_CLOSED_SUCCESS = "Ticket closed successfully.";
}
