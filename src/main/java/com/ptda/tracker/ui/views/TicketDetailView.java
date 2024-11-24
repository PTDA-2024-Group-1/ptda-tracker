package com.ptda.tracker.ui.views;

import com.ptda.tracker.models.assistance.Ticket;
import com.ptda.tracker.models.assistance.TicketReply;
import com.ptda.tracker.services.tracker.TicketService;
import com.ptda.tracker.services.tracker.TicketReplyService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.forms.BudgetForm;
import com.ptda.tracker.ui.forms.TicketForm;
import com.ptda.tracker.ui.forms.TicketReplyForm;
import com.ptda.tracker.ui.renderers.TicketReplyRenderer;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TicketDetailView extends JPanel {
    private final MainFrame mainFrame;
    private final Ticket ticket;
    private final TicketService ticketService;
    private final TicketReplyService ticketReplyService;
    private final JList<TicketReply> repliesList;
    private List<TicketReply> replies;

    public TicketDetailView(MainFrame mainFrame, Ticket ticket) {
        this.mainFrame = mainFrame;
        this.ticket = ticket;
        this.ticketService = mainFrame.getContext().getBean(TicketService.class);
        this.ticketReplyService = mainFrame.getContext().getBean(TicketReplyService.class);

        setLayout(new BorderLayout());

        // Título
        JLabel titleLabel = new JLabel(ticket.getTitle(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Painel Principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Descrição
        JTextArea descriptionArea = new JTextArea(ticket.getBody());
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setBorder(BorderFactory.createTitledBorder("Ticket Description"));
        mainPanel.add(descriptionScroll, BorderLayout.NORTH);

        // Lista de Replies
        repliesList = new JList<>(new DefaultListModel<>());
        repliesList.setCellRenderer(new TicketReplyRenderer());
        refreshRepliesList();
        JScrollPane repliesScroll = new JScrollPane(repliesList);
        repliesScroll.setBorder(BorderFactory.createTitledBorder("Replies"));
        repliesScroll.setPreferredSize(new Dimension(300, 200));
        mainPanel.add(repliesScroll, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Botões de Ação
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addButtons(buttonPanel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addButtons(JPanel buttonPanel) {
        JButton backButton = new JButton("Back");
        styleButton(backButton);
        backButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN));
        buttonPanel.add(backButton);

        JButton editButton = new JButton("Edit Ticket");
        styleButton(editButton);
        editButton.addActionListener(e -> {
            mainFrame.registerScreen(ScreenNames.TICKET_FORM, new TicketForm(mainFrame, null, ticket));
            mainFrame.showScreen(ScreenNames.TICKET_FORM);
        });
        buttonPanel.add(editButton);

        if (ticket.isClosed()) {
            JButton reopenButton = new JButton("Reopen Ticket");
            styleButton(reopenButton);
            reopenButton.addActionListener(e -> {
                TicketReply reply = TicketReply.builder()
                        .ticket(ticket)
                        .createdBy(UserSession.getInstance().getUser())
                        .body("Reopened the ticket.")
                        .build();
                ticketReplyService.save(reply);
                ticket.setClosed(false);
                ticketService.update(ticket);
                JOptionPane.showMessageDialog(this, "Ticket reopened successfully.");
                refreshRepliesList();
                updateButtons(buttonPanel);
            });
            buttonPanel.add(reopenButton);
        } else {
            JButton closeButton = new JButton("Close Ticket");
            styleButton(closeButton);
            closeButton.addActionListener(e -> {
                ticket.setClosed(true);
                ticketService.update(ticket);
                JOptionPane.showMessageDialog(this, "Ticket closed successfully.");
                refreshRepliesList();
                updateButtons(buttonPanel);
            });
            buttonPanel.add(closeButton);
        }

        JButton replyButton = new JButton("Reply");
        styleButton(replyButton);
        replyButton.addActionListener(e -> mainFrame.registerAndShowScreen(ScreenNames.TICKET_REPLY_FORM, new TicketReplyForm(mainFrame, null, ticket)));
        buttonPanel.add(replyButton);
    }

    private void updateButtons(JPanel buttonPanel) {
        buttonPanel.removeAll();
        addButtons(buttonPanel);
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private void refreshRepliesList() {
        replies = ticketReplyService.getAllByTicketId(ticket.getId());
        replies.sort((r1, r2) -> Long.compare(r2.getCreatedAt(), r1.getCreatedAt())); // Ordenar por data de criação (mais recente primeiro)
        DefaultListModel<TicketReply> model = (DefaultListModel<TicketReply>) repliesList.getModel();
        model.clear();
        replies.forEach(model::addElement);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(56, 56, 56));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(140, 40));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0, 0, 0));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(56, 56, 56));
            }
        });
    }
}
