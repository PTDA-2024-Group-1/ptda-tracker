package com.ptda.tracker.ui.user.forms;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.services.tracker.BudgetService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.views.BudgetDetailView;
import com.ptda.tracker.util.LocaleManager;
import com.ptda.tracker.util.ScreenNames;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class BudgetForm extends JPanel {
    private final MainFrame mainFrame;
    private Budget budget;
    private final String returnScreen;
    private final Runnable onFormSubmit;

    public BudgetForm(MainFrame mainFrame, Budget budget, String returnScreen, Runnable onFormSubmit) {
        this.mainFrame = mainFrame;
        this.budget = budget;
        this.returnScreen = returnScreen;
        this.onFormSubmit = (onFormSubmit != null) ? onFormSubmit : () -> {};
        initComponents();
        setListeners();
    }

    private void setListeners() {
        backButton.addActionListener(e -> mainFrame.showScreen(returnScreen));
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
            BudgetService budgetService = mainFrame.getContext().getBean(BudgetService.class);
            if (budget.getId() == null) {
                budgetService.create(budget);
            } else {
                budgetService.update(budget);
            }

            // Go back to Budget Detail View
            onFormSubmit.run();
            mainFrame.registerAndShowScreen(ScreenNames.BUDGET_DETAIL_VIEW, new BudgetDetailView(mainFrame, budget, onFormSubmit));
            JOptionPane.showMessageDialog(this,  BUDGET_SAVED_SUCCESSFULLY + "!", SUCCESS, JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            // Log do erro para debug
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ERROR_OCCURRED_WHILE_SAVING_BUDGET, ERROR, JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel headerLabel = new JLabel(budget == null ? CREATE_NEW_BUDGET : EDIT_BUDGET, SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(headerLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Name Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel(NAME + ":"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField(budget != null ? budget.getName() : "", 25);
        formPanel.add(nameField, gbc);

        // Description Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel(DESCRIPTION + ":"), gbc);

        gbc.gridx = 1;
        descriptionArea = new JTextArea(budget != null ? budget.getDescription() : "", 4, 25);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setPreferredSize(new Dimension(200, 80));
        formPanel.add(descriptionScroll, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonsPanel = new JPanel(new BorderLayout());
        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backButton = new JButton(BACK);
        leftButtonPanel.add(backButton);
        saveButton = new JButton(SAVE);
        rightButtonPanel.add(saveButton);

        buttonsPanel.add(leftButtonPanel, BorderLayout.WEST);
        buttonsPanel.add(rightButtonPanel, BorderLayout.EAST);

        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private JTextField nameField;
    private JTextArea descriptionArea;
    private JButton saveButton;
    private JButton backButton;
    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            CREATE_NEW_BUDGET = localeManager.getTranslation("create_new_budget"),
            EDIT_BUDGET = localeManager.getTranslation("edit_budget"),
            NAME = localeManager.getTranslation("name"),
            DESCRIPTION = localeManager.getTranslation("description"),
            BACK = localeManager.getTranslation("back"),
            SAVE = localeManager.getTranslation("save"),
            VALIDATION_ERROR = localeManager.getTranslation("validation_error"),
            NAME_AND_DESCRIPTION_REQUIRED = localeManager.getTranslation("name_and_description_required"),
            BUDGET_SAVED_SUCCESSFULLY = localeManager.getTranslation("budget_saved_successfully"),
            SUCCESS = localeManager.getTranslation("success"),
            ERROR_OCCURRED_WHILE_SAVING_BUDGET = localeManager.getTranslation("error_occurred_while_saving_budget"),
            ERROR = localeManager.getTranslation("error");

}