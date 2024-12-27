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
    private final String returnScreen;

    public TicketDetailView(MainFrame mainFrame, Ticket ticket, String returnScreen) {
        this.mainFrame = mainFrame;
        this.ticket = ticket;
        this.ticketService = mainFrame.getContext().getBean(TicketService.class);
        this.ticketReplyService = mainFrame.getContext().getBean(TicketReplyService.class);
        this.returnScreen = returnScreen;

        this.mainPanel = new JPanel(new BorderLayout());
        this.closeButton = new JButton(CLOSE_TICKET);
        this.reopenButton = new JButton(REOPEN_TICKET);

        initComponents();
    }

    private void refreshRepliesList() {
        replies = ticketReplyService.getAllByTicketId(ticket.getId());
        DefaultListModel<TicketReply> model = (DefaultListModel<TicketReply>) repliesList.getModel();
        model.clear();
        replies.forEach(model::addElement);

        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = ((JScrollPane) repliesList.getParent().getParent()).getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });

    }

    private JPanel createTitlePanel() {
        JLabel titleLabel = new JLabel(ticket.getTitle(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        return titlePanel;
    }

    private JPanel createMainPanel() {
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        mainPanel.add(createDescriptionPanel(), BorderLayout.NORTH);
        mainPanel.add(createRepliesPanel(), BorderLayout.CENTER);
        initChatPanel();
        mainPanel.add(chatPanel, BorderLayout.SOUTH);
        return mainPanel;
    }

    private JScrollPane createDescriptionPanel() {
        JTextArea descriptionArea = new JTextArea(ticket.getBody());
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setBorder(BorderFactory.createTitledBorder(TICKET_DESCRIPTION));
        return descriptionScroll;
    }

    private JScrollPane createRepliesPanel() {
        repliesList = new JXList(new DefaultListModel<>());
        repliesList.setCellRenderer(new TicketReplyRenderer());
        refreshRepliesList();
        JScrollPane repliesScroll = new JScrollPane(repliesList);
        repliesScroll.setBorder(BorderFactory.createTitledBorder(REPLIES));
        repliesScroll.setPreferredSize(new Dimension(300, 200));
        return repliesScroll;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        updateButtons(buttonPanel);
        return buttonPanel;
    }

    private void updateButtons(JPanel buttonPanel) {
        buttonPanel.removeAll();

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton(BACK);
        backButton.addActionListener(e -> mainFrame.showScreen(returnScreen));
        leftPanel.add(backButton);
        buttonPanel.add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addRightPanelButtons(rightPanel);
        buttonPanel.add(rightPanel, BorderLayout.EAST);

        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private void addRightPanelButtons(JPanel rightPanel) {
        String userType = UserSession.getInstance().getUser().getUserType();
        boolean isFromAssistanceScreen = returnScreen.equals(ScreenNames.ASSISTANCE_SCREEN);

        if (!isFromAssistanceScreen && (userType.equals("USER") || userType.equals("ASSISTANT") || (userType.equals("ADMIN") && !returnScreen.equals(ScreenNames.MANAGE_TICKET_VIEW))) && !ticket.isClosed()) {
            // Show the close button
            closeButton.addActionListener(e -> handleCloseAction());
            rightPanel.add(closeButton);
        }

        if (!isFromAssistanceScreen && ticket.isClosed() && (userType.equals("ADMIN") || userType.equals("ASSISTANT") || userType.equals("USER"))) {
            // Show the reopen button
            reopenButton.addActionListener(e -> handleReopenAction());
            rightPanel.add(reopenButton);
        }

        if (userType.equals("ADMIN") && returnScreen.equals(ScreenNames.MANAGE_TICKET_VIEW) && !ticket.isClosed()) {
            JButton changeAssignmentButton = new JButton(CHANGE_ASSIGNMENT);
            changeAssignmentButton.addActionListener(e -> handleChangeAssignmentAction());
            rightPanel.add(changeAssignmentButton);
        }
    }

    private void handleReopenAction() {
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
        updateButtons((JPanel) getComponent(2));
        updateChatPanel();
    }

    private void handleCloseAction() {
        ticket.setClosed(true);
        ticketService.update(ticket);

        JOptionPane.showMessageDialog(this, TICKET_CLOSED_SUCCESS);
        refreshRepliesList();
        updateButtons((JPanel) getComponent(2));
        mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN);
    }

    private void handleChangeAssignmentAction() {
        ChangeAssignmentDialog dialog = new ChangeAssignmentDialog(mainFrame, ticket);
        dialog.setVisible(true);
    }

    private void initChatPanel() {
        chatPanel = new JPanel(new BorderLayout());

        String userType = UserSession.getInstance().getUser().getUserType();
        boolean isAdminInManageView = userType.equals("ADMIN") && returnScreen.equals(ScreenNames.MANAGE_TICKET_VIEW);

        if ((userType.equals("USER") || userType.equals("ASSISTANT") || (userType.equals("ADMIN") && !isAdminInManageView)) && !ticket.isClosed()) {
            chatPanel.setBorder(BorderFactory.createTitledBorder(REPLY));

            JTextArea chatArea = new JTextArea();
            chatArea.setLineWrap(true);
            chatArea.setWrapStyleWord(true);
            chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
            chatArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            JButton sendButton = new JButton("Send");
            sendButton.addActionListener(e -> handleSendAction(chatArea));

            inputPanel = new JPanel(new BorderLayout(10, 0));
            inputPanel.add(chatArea, BorderLayout.CENTER);
            inputPanel.add(sendButton, BorderLayout.EAST);

            chatPanel.add(inputPanel, BorderLayout.CENTER);
        }
    }

    private void handleSendAction(JTextArea chatArea) {
        String replyBody = chatArea.getText().trim();
        if (!replyBody.isEmpty()) {
            if (ticket.getAssistant() == null && UserSession.getInstance().getUser() instanceof Assistant) {
                Assistant currentUser = (Assistant) UserSession.getInstance().getUser();
                if (!currentUser.equals(ticket.getCreatedBy())) {
                    ticket.setAssistant(currentUser);
                    ticketService.update(ticket);
                    AssistanceScreen.refreshTicketLists();
                }
            }

            TicketReply reply = TicketReply.builder()
                    .ticket(ticket)
                    .createdBy(UserSession.getInstance().getUser())
                    .body(replyBody)
                    .build();
            ticketReplyService.create(reply);
            chatArea.setText("");
            refreshRepliesList();
        }
    }

    private void updateChatPanel() {
        mainPanel.remove(chatPanel);
        initChatPanel();
        mainPanel.add(chatPanel, BorderLayout.SOUTH);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private List<TicketReply> replies;
    private JXList repliesList;
    private final JPanel mainPanel;
    private JPanel inputPanel;
    private JPanel chatPanel;
    private final JButton closeButton;
    private final JButton reopenButton;

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
