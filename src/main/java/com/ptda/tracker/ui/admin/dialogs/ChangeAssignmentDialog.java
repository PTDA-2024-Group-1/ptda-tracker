package com.ptda.tracker.ui.admin.dialogs;

import com.ptda.tracker.models.assistance.Assistant;
import com.ptda.tracker.models.assistance.Ticket;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.assistance.AssistantService;
import com.ptda.tracker.services.assistance.TicketService;
import com.ptda.tracker.services.user.UserService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.LocaleManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChangeAssignmentDialog extends JDialog {

    public ChangeAssignmentDialog(MainFrame mainFrame, Ticket ticket) {
        super(mainFrame, CHANGE_ASSIGNMENT, true); // Mantém o título na barra da janela
        this.ticket = ticket;
        this.ticketService = mainFrame.getContext().getBean(TicketService.class);
        this.userService = mainFrame.getContext().getBean(UserService.class);
        initComponents();
    }

    private void initComponents() {
        // Configurações do Dialog
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Layout principal com margens
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        // Painel do formulário (mantido como antes, sem o título)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Label "Selecionar Assistente"
        JLabel selectAssistantLabel = new JLabel(SELECT_ASSISTANT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(selectAssistantLabel, gbc);

        // ComboBox de Assistentes
        assistantComboBox = new JComboBox<>();
        List<User> assistants = userService.getAllUsers().stream()
                .filter(user -> user.getUserType().equals("ASSISTANT"))
                .toList();
        Map<Long, Long> assistantTicketCounts = ticketService.getAll().stream()
                .filter(t -> t.getAssistant() != null && !t.isClosed())
                .collect(Collectors.groupingBy(t -> t.getAssistant().getId(), Collectors.counting()));

        for (User assistant : assistants) {
            long ticketCount = assistantTicketCounts.getOrDefault(assistant.getId(), 0L);
            assistantComboBox.addItem(assistant.getName() + " (" + ticketCount + " tickets ativos)");
        }
        if (ticket.getAssistant() != null) {
            long ticketCount = assistantTicketCounts.getOrDefault(ticket.getAssistant().getId(), 0L);
            assistantComboBox.setSelectedItem(ticket.getAssistant().getName() + " (" + ticketCount + " tickets ativos)");
        }
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(assistantComboBox, gbc);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Painel de botões (botão com estilo padrão)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton saveButton = new JButton(SAVE); // Estilo padrão do botão
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedAssistantName = (String) assistantComboBox.getSelectedItem();
                User selectedAssistant = assistants.stream()
                        .filter(a -> selectedAssistantName.startsWith(a.getName()))
                        .findFirst()
                        .orElse(null);
                ticket.setAssistant((Assistant) selectedAssistant);
                ticketService.update(ticket);
                JOptionPane.showMessageDialog(ChangeAssignmentDialog.this, ASSIGNMENT_UPDATING_ASSIGNMENT);
                dispose();
            }
        });
        buttonPanel.add(saveButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(getParent());
    }
    private final Ticket ticket;
    private final TicketService ticketService;
    private final UserService userService;
    private JComboBox<String> assistantComboBox;
    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            ASSIGNMENT_UPDATING_ASSIGNMENT = localeManager.getTranslation("updating.assignment"),
            CHANGE_ASSIGNMENT = localeManager.getTranslation("change.assignment"),
            SELECT_ASSISTANT = localeManager.getTranslation("select.assistant"),
            SAVE = localeManager.getTranslation("save");
}