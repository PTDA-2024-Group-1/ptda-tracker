package com.ptda.tracker.ui.admin.dialogs;

import com.ptda.tracker.models.assistance.Assistant;
import com.ptda.tracker.models.assistance.Ticket;
import com.ptda.tracker.services.assistance.AssistantService;
import com.ptda.tracker.services.assistance.TicketService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.admin.renderers.AssistantRenderer;
import com.ptda.tracker.util.LocaleManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ChangeAssignmentDialog extends JDialog {
    private final Ticket ticket;
    private final TicketService ticketService;
    private final AssistantService assistantService;
    private JComboBox<Assistant> assistantComboBox;

    public ChangeAssignmentDialog(MainFrame mainFrame, Ticket ticket) {
        super(mainFrame, CHANGE_ASSIGNMENT, true);
        this.ticket = ticket;
        this.ticketService = mainFrame.getContext().getBean(TicketService.class);
        this.assistantService = mainFrame.getContext().getBean(AssistantService.class);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10)); // Add spacing between components

        // Main Panel to hold the form
        JPanel formPanel = new JPanel(new GridLayout(1, 2, 5, 5)); // 1 row, 2 columns, with gaps

        // Select Assistant Label
        JLabel selectAssistantLabel = new JLabel(SELECT_ASSISTANT);
        formPanel.add(selectAssistantLabel);

        // Assistant ComboBox
        assistantComboBox = new JComboBox<>();
        assistantComboBox.setRenderer(new AssistantRenderer());
        List<Assistant> assistants = assistantService.getAll();
        assistants.forEach(assistantComboBox::addItem);

        // Set the currently assigned assistant as selected
        if (ticket.getAssistant() != null) {
            assistantComboBox.setSelectedItem(ticket.getAssistant());
        }
        formPanel.add(assistantComboBox);

        add(formPanel, BorderLayout.CENTER);

        // Save Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton(SAVE);
        saveButton.addActionListener(e -> {
            Assistant selectedAssistant = (Assistant) assistantComboBox.getSelectedItem();
            ticket.setAssistant(selectedAssistant);
            ticketService.update(ticket);
            JOptionPane.showMessageDialog(this, ASSIGNMENT_UPDATING_ASSIGNMENT);
            dispose();
        });
        buttonPanel.add(saveButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Dialog settings
        setSize(300, 150);
        setLocationRelativeTo(getParent());
    }

    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            ASSIGNMENT_UPDATING_ASSIGNMENT = localeManager.getTranslation("updating.assignment"),
            CHANGE_ASSIGNMENT = localeManager.getTranslation("change.assignment"),
            SELECT_ASSISTANT = localeManager.getTranslation("select.assistant"),
            SAVE = localeManager.getTranslation("save");

}
