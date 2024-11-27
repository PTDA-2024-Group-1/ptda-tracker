package com.ptda.tracker.ui.forms;

import com.ptda.tracker.models.assistance.Ticket;
import com.ptda.tracker.services.tracker.TicketService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.views.TicketDetailView;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;

public class TicketForm extends JPanel {
    private final MainFrame mainFrame;
    private final Runnable onSaveCallback;
    private final Ticket existingTicket;
    private final TicketService ticketService;
    private JTextField titleField;
    private JTextArea descriptionArea;

    private static final Color PRIMARY_COLOR = new Color(240, 240, 240);

    public TicketForm(MainFrame mainFrame, Runnable onSaveCallback, Ticket existingTicket) {
        this.mainFrame = mainFrame;
        this.onSaveCallback = onSaveCallback;
        this.existingTicket = existingTicket;
        this.ticketService = mainFrame.getContext().getBean(TicketService.class);

        setLayout(new BorderLayout(20, 20));
        setBackground(PRIMARY_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initUI();
    }

    private void initUI() {
        // Header
        JLabel headerLabel = new JLabel("Create New Ticket");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerLabel.setForeground(Color.DARK_GRAY);
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(headerLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(PRIMARY_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Title:"), gbc);

        gbc.gridx = 1;
        titleField = new JTextField(existingTicket != null ? existingTicket.getTitle() : "", 25);
        formPanel.add(titleField, gbc);

        // Description Area
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        descriptionArea = new JTextArea(existingTicket != null ? existingTicket.getBody() : "", 4, 25);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setPreferredSize(new Dimension(200, 80));
        formPanel.add(descriptionScroll, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(PRIMARY_COLOR);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN));

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveTicket());

        buttonPanel.add(backButton);
        buttonPanel.add(saveButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveTicket() {
        String title = titleField.getText().trim();
        String body = descriptionArea.getText().trim();

        if (title.isEmpty() || body.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title and Description cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Ticket ticket = (existingTicket == null) ? new Ticket() : existingTicket;

        ticket.setTitle(title);
        ticket.setBody(body);
        ticket.setCreatedBy(UserSession.getInstance().getUser()); // Authenticated user
        ticket.setAssistant(null); // Or use logic to select an assistant, if applicable.

        ticketService.save(ticket);

        if (onSaveCallback != null) {
            onSaveCallback.run();
        }
        mainFrame.registerAndShowScreen(ScreenNames.TICKET_DETAIL_VIEW, new TicketDetailView(mainFrame, ticket));
    }

    private void clear() {
        titleField.setText("");
        descriptionArea.setText("");
    }
}