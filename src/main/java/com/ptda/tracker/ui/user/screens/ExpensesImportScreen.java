package com.ptda.tracker.ui.user.screens;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.dialogs.expenses.*;
import com.ptda.tracker.ui.user.forms.ExpensesEditForm;
import com.ptda.tracker.util.ExpensesConverter;
import com.ptda.tracker.util.ExpensesImportSharedData;
import com.ptda.tracker.util.ScreenNames;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.List;

public class ExpensesImportScreen extends JPanel {
    private final MainFrame mainFrame;
    private final Budget budget;
    private final String returnScreen;
    private final Runnable onImportSuccess;
    private final ExpensesImportSharedData sharedData;

    public ExpensesImportScreen(MainFrame mainFrame, Budget budget, String returnScreen, Runnable onImportSuccess) {
        this.mainFrame = mainFrame;
        this.budget = budget;
        this.returnScreen = returnScreen;
        this.onImportSuccess = onImportSuccess;
        this.sharedData = ExpensesImportSharedData.getInstance();
        sharedData.setDefaultBudget(budget);
        initComponents();
        setListeners();
        new Thread(this::openSourceSelectorDialog).start();
    }

    private void setListeners() {
        cancelButton.addActionListener(e -> cancelImport());
        restartImportButton.addActionListener(e -> restartImport());
        columnMappingButton.addActionListener(e -> openColumnMappingDialog());
        categoryMappingButton.addActionListener(e -> openCategoryMappingDialog());
        dateFormatButton.addActionListener(e -> openDateFormatDialog());
        valueTreatmentButton.addActionListener(e -> openValueTreatmentDialog());
        nextButton.addActionListener(e -> passToExpensesEditForm());
    }

    private void cancelImport() {
        if (sharedData.getRawData() == null || sharedData.getRawData().isEmpty()) {
            mainFrame.removeScreen(ScreenNames.EXPENSES_IMPORT);
            mainFrame.showScreen(returnScreen);
            return;
        }

        int response = JOptionPane.showConfirmDialog(
                this,
                "Do you want to keep the progress?",
                "Cancel Import",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (response == JOptionPane.NO_OPTION) {
            // Reset the shared data and navigate back to the previous screen
            ExpensesImportSharedData.resetInstance();
            mainFrame.removeScreen(ScreenNames.EXPENSES_IMPORT);
            mainFrame.showScreen(returnScreen);
        } else if (response == JOptionPane.YES_OPTION) {
            // Navigate back without resetting the form
            mainFrame.showScreen(returnScreen);
        }
        // If CANCEL_OPTION or CLOSED_OPTION, do nothing
    }

    private void restartImport() {
        mainFrame.removeScreen(ScreenNames.EXPENSES_IMPORT);
        ExpensesImportSharedData.resetInstance();
        mainFrame.registerAndShowScreen(
                ScreenNames.EXPENSES_IMPORT,
                new ExpensesImportScreen(
                        mainFrame,
                        budget,
                        returnScreen,
                        onImportSuccess
                )
        );
    }

    private void updatePreviewTable() {
        String[] columnNames;
        String[][] data;

        if (sharedData.isHasHeader()) {
            columnNames = sharedData.getRawData().getFirst();
            data = new String[sharedData.getRawData().size() - 1][columnNames.length];
            for (int i = 1; i < sharedData.getRawData().size(); i++) {
                data[i - 1] = sharedData.getRawData().get(i);
            }
        } else {
            int columnCount = sharedData.getRawData().getFirst().length;
            columnNames = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                columnNames[i] = COLUMN + " " + (i + 1);
            }
            data = new String[sharedData.getRawData().size()][columnCount];
            for (int i = 0; i < sharedData.getRawData().size(); i++) {
                data[i] = sharedData.getRawData().get(i);
            }
        }

        TableModel model = new DefaultTableModel(data, columnNames);
        expensesTable.setModel(model);

        if (!stepsPanel.isVisible()) {
            stepsPanel.setVisible(true);
        }
    }

    private void manageSteps() {
        boolean hasColumnsMapping = sharedData.getColumnMapping() != null && !sharedData.getColumnMapping().isEmpty();
        boolean hasCategoryColumn = hasColumnsMapping && sharedData.getColumnMapping().containsKey(ImportColumnsDialog.ExpenseFieldOptions.CATEGORY.toString());

        // Activate step buttons
        columnMappingButton.setEnabled(true);
        categoryMappingButton.setEnabled(hasCategoryColumn);
        dateFormatButton.setEnabled(hasColumnsMapping);
        valueTreatmentButton.setEnabled(hasColumnsMapping);

        // Update "Next" button state
        verifyNextButton();
    }


    private void verifyNextButton() {
        boolean hasData = sharedData.getRawData() != null && !sharedData.getRawData().isEmpty();
        boolean hasColumnsMapping = sharedData.getColumnMapping() != null && !sharedData.getColumnMapping().isEmpty();
        boolean hasCategoryColumn = hasColumnsMapping && sharedData.getColumnMapping().containsKey(ImportColumnsDialog.ExpenseFieldOptions.CATEGORY.toString());
        boolean enableNext = isEnableNext(hasData, hasColumnsMapping, hasCategoryColumn);

        nextButton.setEnabled(enableNext);
        if (enableNext) {
            nextButton.requestFocus();
        }
    }

    private boolean isEnableNext(boolean hasData, boolean hasColumnsMapping, boolean hasCategoryColumn) {
        boolean hasCategoryMapping = sharedData.getCategoryMapping() != null && !sharedData.getCategoryMapping().isEmpty();
        boolean hasDateFormat = sharedData.getDateFormat() != null && !sharedData.getDateFormat().isEmpty();
        boolean hasValueTreatment = sharedData.getValueTreatment() != null;

        // Enable 'Next' button if all essential conditions are met
        boolean enableNext = hasData && hasColumnsMapping && hasDateFormat && hasValueTreatment;
        if (hasCategoryColumn) {
            enableNext = enableNext && hasCategoryMapping;
        }
        return enableNext;
    }

    private void openSourceSelectorDialog() {
        ImportSourceDialog dialog = new ImportSourceDialog(mainFrame, this::onSourceSelected);
        dialog.setVisible(true);
    }

    private void onSourceSelected() {
        updatePreviewTable();
        manageSteps();
        openColumnMappingDialog();
    }

    private void openColumnMappingDialog() {
        ImportColumnsDialog dialog = new ImportColumnsDialog(mainFrame, this::onColumnMappingSuccess);
        dialog.setVisible(true);
    }

    private void onColumnMappingSuccess() {
        manageSteps();
        executeDialogs();
    }

    private void openCategoryMappingDialog() {
        ImportCategoriesDialog dialog = new ImportCategoriesDialog(mainFrame, this::manageSteps);
        dialog.setVisible(true);
    }

    private void openDateFormatDialog() {
        ImportDateFormatDialog dialog = new ImportDateFormatDialog(mainFrame, this::manageSteps);
        dialog.setVisible(true);
    }

    private void openValueTreatmentDialog() {
        ImportValueTreatmentDialog dialog = new ImportValueTreatmentDialog(mainFrame, this::manageSteps);
        dialog.setVisible(true);
    }

    private void executeDialogs() {
        boolean hasCategoryColumn = sharedData.getColumnMapping() != null
                && sharedData.getColumnMapping().containsKey(ImportColumnsDialog.ExpenseFieldOptions.CATEGORY.toString());
        if (hasCategoryColumn) {
            openCategoryMappingDialog();
        }
        openDateFormatDialog();
        openValueTreatmentDialog();
    }

    private void passToExpensesEditForm() {
        int response = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to proceed with the import?",
                "Confirm Import",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (response != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            List<Expense> expenses = ExpensesConverter.transformImportData(sharedData);
            String currentScreen = mainFrame.getCurrentScreen();
            sharedData.setRawData(null);
            mainFrame.removeScreen(currentScreen);
            mainFrame.registerAndShowScreen(
                    currentScreen,
                    new ExpensesEditForm(mainFrame, expenses, budget, returnScreen, this::onSubmitSuccess)
            );
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error processing the imported data. Please check the mappings and formats.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void onSubmitSuccess() {
        ExpensesImportSharedData.resetInstance();
        onImportSuccess.run();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Steps
        stepsPanel = new JPanel(new FlowLayout());
        columnMappingButton = new JButton(COLUMN_MAPPING);
        categoryMappingButton = new JButton(CATEGORY_MAPPING);
        dateFormatButton = new JButton(DATE_FORMAT);
        valueTreatmentButton = new JButton(VALUE_TREATMENT);

        stepsPanel.add(columnMappingButton);
        stepsPanel.add(categoryMappingButton);
        stepsPanel.add(dateFormatButton);
        stepsPanel.add(valueTreatmentButton);
        stepsPanel.setVisible(false);

        add(stepsPanel, BorderLayout.NORTH);

        // Table setup
        expensesTable = new JTable();
        expensesTable.getTableHeader().setReorderingAllowed(false);
        expensesTable.getTableHeader().setResizingAllowed(false);
        expensesTable.setEnabled(false);
        add(new JScrollPane(expensesTable), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        cancelButton = new JButton(CANCEL);
        restartImportButton = new JButton(RESTART_IMPORT);
        nextButton = new JButton(NEXT);
        nextButton.setEnabled(false);

        buttonPanel.add(cancelButton);
        buttonPanel.add(restartImportButton);
        buttonPanel.add(nextButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel stepsPanel;
    private JTable expensesTable;
    private JButton cancelButton, restartImportButton, nextButton,
            columnMappingButton, categoryMappingButton, dateFormatButton, valueTreatmentButton;
    private static final String
            CANCEL = "Cancel",
            RESTART_IMPORT = "Restart Import",
            COLUMN_MAPPING = "Map Columns",
            CATEGORY_MAPPING = "Map Categories",
            DATE_FORMAT = "Set Date Format",
            VALUE_TREATMENT = "Set Value Treatment",
            NEXT = "Next",
            COLUMN = "Column";
}
