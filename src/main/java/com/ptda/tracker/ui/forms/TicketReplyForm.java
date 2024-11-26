package com.ptda.tracker.ui.forms;

import com.ptda.tracker.models.assistance.Ticket;
import com.ptda.tracker.models.assistance.TicketReply;
import com.ptda.tracker.services.tracker.TicketReplyService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.views.TicketDetailView;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;

public class TicketReplyForm extends JPanel {
    private final MainFrame mainFrame;
    private final Ticket ticket;
    private final TicketReplyService ticketReplyService;
    private final Runnable onFormSubmit;

    private JTextArea replyBodyArea;

    public TicketReplyForm(MainFrame mainFrame, Runnable onFormSubmit, Ticket ticket) {
        this.mainFrame = mainFrame;
        this.ticket = ticket;
        this.onFormSubmit = (onFormSubmit != null) ? onFormSubmit : () -> {};
        this.ticketReplyService = mainFrame.getContext().getBean(TicketReplyService.class);

        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Reply to Ticket: " + ticket.getTitle(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        replyBodyArea = new JTextArea();
        replyBodyArea.setLineWrap(true);
        replyBodyArea.setWrapStyleWord(true);
        add(new JScrollPane(replyBodyArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton saveButton = new JButton("Save Reply");
        styleButton(saveButton);
        saveButton.addActionListener(e -> saveReply());


        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton);
        cancelButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.TICKET_DETAIL_VIEW));

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveReply() {
        String replyBody = replyBodyArea.getText().trim();
        if (replyBody.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Reply body cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        TicketReply reply = TicketReply.builder()
                .ticket(ticket)
                .createdBy(UserSession.getInstance().getUser())
                .body(replyBody)
                .build();
        ticketReplyService.save(reply);
        JOptionPane.showMessageDialog(this, "Reply saved successfully.");
        onFormSubmit.run();
        mainFrame.registerAndShowScreen(ScreenNames.TICKET_DETAIL_VIEW, new TicketDetailView(mainFrame, ticket));
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(56, 56, 56));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 40));
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