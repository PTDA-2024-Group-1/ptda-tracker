package com.ptda.tracker.ui.forms;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.services.tracker.BudgetService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.views.BudgetDetailView;
import com.ptda.tracker.util.ScreenNames;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class BudgetForm extends JPanel {
    private final MainFrame mainFrame;
    private final ApplicationContext context;
    private final Runnable onFormSubmit;
    private Budget budget;
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JButton saveButton, cancelButton;

    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);

    private static final String
            CREATE_NEW_BUDGET = "Create New Budget",
            EDIT_BUDGET = "Edit Budget",
            NAME = "Name",
            DESCRIPTION = "Description",
            CANCEL = "Cancel",
            SAVE = "Save",
            VALIDATION_ERROR = "Validation Error",
            NAME_AND_DESCRIPTION_REQUIRED = "Name and description are required",
            BUDGET_SAVED_SUCCESSFULLY = "Budget saved successfully",
            SUCCESS = "Success",
            ERROR_OCCURRED_WHILE_SAVING_BUDGET = "An error occurred while saving the budget. Please try again",
            ERROR = "Error";

    public BudgetForm(MainFrame mainFrame, Runnable onFormSubmit, Budget budget) {
        this.mainFrame = mainFrame;
        this.context = mainFrame.getContext();
        this.onFormSubmit = (onFormSubmit != null) ? onFormSubmit : () -> {};
        this.budget = budget;

        initUI();
        setListeners();
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel headerLabel = new JLabel(budget == null ? CREATE_NEW_BUDGET : EDIT_BUDGET, SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerLabel.setForeground(Color.DARK_GRAY);
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(headerLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Name Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel(NAME + ":"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField(budget != null ? budget.getName() : "", 25);
        nameField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        nameField.setBackground(Color.WHITE);
        nameField.setForeground(Color.BLACK);
        nameField.setCaretColor(Color.BLACK);
        formPanel.add(nameField, gbc);

        // Description Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel(DESCRIPTION + ":"), gbc);

        gbc.gridx = 1;
        descriptionArea = new JTextArea(budget != null ? budget.getDescription() : "", 4, 25);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setForeground(Color.BLACK);
        descriptionArea.setCaretColor(Color.BLACK);
        descriptionArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setPreferredSize(new Dimension(200, 80));
        formPanel.add(descriptionScroll, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        cancelButton = new JButton(CANCEL);
        buttonPanel.add(cancelButton);
        saveButton = new JButton(SAVE);
        buttonPanel.add(saveButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setListeners() {
        cancelButton.addActionListener(e -> {
            if (budget == null) {
                // Se o orçamento for null, estamos em criação, então volta para a tela de navegação
                mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN);
            } else {
                // Se o orçamento já existe, estamos em edição, então volta para o BudgetDetailView
                mainFrame.registerAndShowScreen(ScreenNames.BUDGET_DETAIL_VIEW, new BudgetDetailView(mainFrame, budget));
            }
        });
        saveButton.addActionListener(this::saveBudget);
    }

    private void saveBudget(ActionEvent e) {
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();

        // Validation
        if (name.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, NAME_AND_DESCRIPTION_REQUIRED, VALIDATION_ERROR, JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Budget preparation
        if (budget == null) {
            budget = new Budget();
        }
        budget.setName(name);
        budget.setDescription(description);

        // Save budget
        try {
            BudgetService budgetService = context.getBean(BudgetService.class);
            if (budget.getId() == null) {
                budgetService.create(budget);
            } else {
                budgetService.update(budget);
            }

            // Go back to Budget Detail View
            onFormSubmit.run();
            mainFrame.registerAndShowScreen(ScreenNames.BUDGET_DETAIL_VIEW, new BudgetDetailView(mainFrame, budget));
            JOptionPane.showMessageDialog(this,  BUDGET_SAVED_SUCCESSFULLY + "!", SUCCESS, JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            // Log do erro para debug
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ERROR_OCCURRED_WHILE_SAVING_BUDGET, ERROR, JOptionPane.ERROR_MESSAGE);
        }
    }
}