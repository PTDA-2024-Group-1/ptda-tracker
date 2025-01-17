package com.ptda.tracker.ui.user.forms;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.BudgetAccessLevel;
import com.ptda.tracker.services.tracker.BudgetAccessService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.views.BudgetDetailView;
import com.ptda.tracker.util.LocaleManager;
import com.ptda.tracker.util.ScreenNames;

import javax.swing.*;
import java.awt.*;

public class ShareBudgetForm extends JPanel {
    private final MainFrame mainFrame;
    private final BudgetAccessService budgetAccessService;
    private final Budget budget;

    public ShareBudgetForm(MainFrame mainFrame, Budget budget) {
        this.mainFrame = mainFrame;
        this.budget = budget;
        budgetAccessService = mainFrame.getContext().getBean(BudgetAccessService.class);
        initComponents();
        setListeners();
    }

    private void setListeners() {
        backButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.BUDGET_DETAIL_VIEW));
        saveButton.addActionListener(e -> addParticipant());
    }

    private void addParticipant() {
        String email = emailField.getText().trim();
        BudgetAccessLevel accessLevel = (BudgetAccessLevel) accessLevelComboBox.getSelectedItem();

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, EMAIL_REQUIRED,
                    ERROR, JOptionPane.ERROR_MESSAGE
            ); return;
        }

        if (budgetAccessService.hasAccess(budget.getId(), email, BudgetAccessLevel.VIEWER)) {
            JOptionPane.showMessageDialog(
                    this, "This user already has access to the budget",
                    ERROR, JOptionPane.ERROR_MESSAGE
            ); return;
        }

        try {
            budgetAccessService.create(budget.getId(), email, accessLevel);
            JOptionPane.showMessageDialog(this, PARTICIPANT_ADDED_SUCCESSFULLY, SUCCESS, JOptionPane.INFORMATION_MESSAGE);
            mainFrame.showScreen(ScreenNames.BUDGET_DETAIL_VIEW);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, FAILED_TO_ADD_PARTICIPANT + ":" + e.getMessage(), ERROR, JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel(SHARE_BUDGET, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Email Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel(USER_EMAIL + ":"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Access Level Dropdown
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel(ACCESS_LEVEL + ":"), gbc);

        gbc.gridx = 1;
        accessLevelComboBox = new JComboBox<>(BudgetAccessLevel.values());
        formPanel.add(accessLevelComboBox, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonsPanel = new JPanel(new BorderLayout());
        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        backButton = new JButton(BACK);
        leftButtonPanel.add(backButton);

        saveButton = new JButton(ADD_PARTICIPANT);
        rightButtonPanel.add(saveButton);

        buttonsPanel.add(leftButtonPanel, BorderLayout.WEST);
        buttonsPanel.add(rightButtonPanel, BorderLayout.EAST);

        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private JComboBox<BudgetAccessLevel> accessLevelComboBox;
    private JTextField emailField;
    private JButton backButton, saveButton;
    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            SHARE_BUDGET = localeManager.getTranslation("share_budget"),
            USER_EMAIL = localeManager.getTranslation("user_email"),
            ACCESS_LEVEL = localeManager.getTranslation("access_level"),
            BACK = localeManager.getTranslation("back"),
            ADD_PARTICIPANT = localeManager.getTranslation("add_participant"),
            EMAIL_REQUIRED = localeManager.getTranslation("email_required"),
            ERROR = localeManager.getTranslation("error"),
            PARTICIPANT_ADDED_SUCCESSFULLY = localeManager.getTranslation("participant_added_successfully"),
            SUCCESS = localeManager.getTranslation("success"),
            FAILED_TO_ADD_PARTICIPANT = localeManager.getTranslation("failed_to_add_participant");
}