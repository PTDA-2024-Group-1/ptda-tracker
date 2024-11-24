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

import static com.ptda.tracker.ui.forms.ExpenseForm.getJButton;

public class BudgetForm extends JPanel {
    private final MainFrame mainFrame;
    private final ApplicationContext context;
    private final Runnable onFormSubmit;
    private Budget budget;
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JButton saveButton, backButton;

    private static final Color PRIMARY_COLOR = new Color(240, 240, 240); // Fundo claro
    private static final Color BUTTON_COLOR = new Color(56, 56, 56, 255); // Cor padrão do botão (#383838FF)
    private static final Color BUTTON_HOVER_COLOR = new Color(0, 0, 0, 255); // Preto no hover (#000000FF)
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE; // Texto branco no hover

    public BudgetForm(MainFrame mainFrame, Runnable onFormSubmit, Budget budget) {
        this.mainFrame = mainFrame;
        this.context = mainFrame.getContext();
        this.onFormSubmit = (onFormSubmit != null) ? onFormSubmit : () -> {};
        this.budget = budget;

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(PRIMARY_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel headerLabel = new JLabel(budget == null ? "Create New Budget" : "Edit Budget");
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

        // Name Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);

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
        formPanel.add(new JLabel("Description:"), gbc);

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
        buttonPanel.setBackground(PRIMARY_COLOR);

        // Back Button
        backButton = createStyledButton("Back");
        backButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.BUDGET_DETAIL_VIEW));

        // Save Button
        saveButton = createStyledButton("Save");
        saveButton.addActionListener(this::saveBudget);

        buttonPanel.add(backButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveBudget(ActionEvent e) {
        try {
            // Validação inicial
            if (nameField.getText().trim().isEmpty() || descriptionArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and description are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Obtenção do serviço e preparação do objeto Budget
            BudgetService budgetService = context.getBean(BudgetService.class);

            if (budget == null) {
                budget = new Budget();
            }

            budget.setName(nameField.getText().trim());
            budget.setDescription(descriptionArea.getText().trim());

            // Salvar o orçamento
            if (budget.getId() == null) {
                budgetService.create(budget);
            } else {
                budgetService.update(budget);
            }

            // Mensagem de sucesso
            JOptionPane.showMessageDialog(this, "Budget saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Atualizar a interface
            onFormSubmit.run();
            mainFrame.registerAndShowScreen(ScreenNames.BUDGET_DETAIL_VIEW, new BudgetDetailView(mainFrame, budget));
        } catch (Exception ex) {
            // Log do erro para debug
            ex.printStackTrace(); // TO DO: Substituir por um logger no futuro

            // Mensagem de erro amigável para o usuário
            JOptionPane.showMessageDialog(this, "An error occurred while saving the budget. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createStyledButton(String text) {
        return getJButton(text, BUTTON_COLOR, BUTTON_HOVER_COLOR, BUTTON_TEXT_COLOR);
    }
}
