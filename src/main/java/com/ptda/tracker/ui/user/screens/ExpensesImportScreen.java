package com.ptda.tracker.ui.user.screens;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.dialogs.expenses.*;
import com.ptda.tracker.ui.user.forms.ExpensesEditForm;
import com.ptda.tracker.util.ExpensesConverter;
import com.ptda.tracker.util.ImportSharedData;
import com.ptda.tracker.util.ScreenNames;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.text.ParseException;
import java.util.List;

public class ExpensesImportScreen extends JPanel {
    private final MainFrame mainFrame;
    private final Budget budget;
    private final String returnScreen;
    private final Runnable onImportSuccess;
    private final ImportSharedData sharedData;

    public ExpensesImportScreen(MainFrame mainFrame, Budget budget, String returnScreen, Runnable onImportSuccess) {
        this.mainFrame = mainFrame;
        this.budget = budget;
        this.returnScreen = returnScreen;
        this.onImportSuccess = onImportSuccess;
        this.sharedData = ImportSharedData.getInstance();
        new Thread(this::openSourceSelectorDialog).start();
        initComponents();
        setListeners();
    }

    private void setListeners() {
        cancelButton.addActionListener(e -> cancelImport());
        restartImportButton.addActionListener(e -> restartImport());
        columnMappingButton.addActionListener(e -> openColumnMappingDialog());
        categoryMappingButton.addActionListener(e -> openCategoryMappingDialog());
        dateFormatButton.addActionListener(e -> openDateFormatDialog());
        valueTreatmentButton.addActionListener(e -> openValueTreatmentDialog());
        nextButton.addActionListener(e -> openExpensesEditForm());
    }

    private void cancelImport() {
        int response = JOptionPane.showConfirmDialog(
                this,
                "Do you want to reset the form?",
                "Cancel Import",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            // Reset the shared data and navigate back to the previous screen
            ImportSharedData.resetInstance();
            mainFrame.removeScreen(ScreenNames.EXPENSES_IMPORT_SCREEN);
            mainFrame.showScreen(returnScreen);
        } else if (response == JOptionPane.NO_OPTION) {
            // Navigate back without resetting the form
            mainFrame.showScreen(returnScreen);
        }
        // If CANCEL_OPTION or CLOSED_OPTION, do nothing
    }

    private void restartImport() {
        mainFrame.removeScreen(ScreenNames.EXPENSES_IMPORT_SCREEN);
        ImportSharedData.resetInstance();
        mainFrame.registerAndShowScreen(
                ScreenNames.EXPENSES_IMPORT_SCREEN,
                new ExpensesImportScreen(
                        mainFrame,
                        budget,
                        returnScreen,
                        onImportSuccess
                )
        );
    }

    private void openSourceSelectorDialog() {
        ImportSourceDialog dialog = new ImportSourceDialog(mainFrame, this::updatePreviewTable);
        dialog.setVisible(true);
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

    private void verifyNextButton() {
        boolean hasData = sharedData.getRawData() != null && !sharedData.getRawData().isEmpty();
        boolean hasColumnsMapping = sharedData.getColumnMapping() != null && !sharedData.getColumnMapping().isEmpty();
        boolean hasCategoryColumn = hasColumnsMapping && sharedData.getColumnMapping().containsKey(ImportColumnsDialog.ExpenseFieldOptions.CATEGORY.toString());
        boolean hasCategoryMapping = sharedData.getCategoryMapping() != null && !sharedData.getCategoryMapping().isEmpty();
        boolean hasDateFormat = sharedData.getDateFormat() != null && !sharedData.getDateFormat().isEmpty();

        // Enable 'Next' button if all essential conditions are met
        boolean enableNext = hasData && hasColumnsMapping && hasDateFormat;
        if (hasCategoryColumn) {
            enableNext = enableNext && hasCategoryMapping;
        }

        nextButton.setEnabled(enableNext);
    }

    private void openColumnMappingDialog() {
        ImportColumnsDialog dialog = new ImportColumnsDialog(mainFrame, this::verifyNextButton);
        dialog.setVisible(true);
    }

    private void openCategoryMappingDialog() {
        ImportCategoriesDialog dialog = new ImportCategoriesDialog(mainFrame, this::verifyNextButton);
        dialog.setVisible(true);
    }

    private void openDateFormatDialog() {
        ImportDateFormatDialog dialog = new ImportDateFormatDialog(mainFrame, this::verifyNextButton);
        dialog.setVisible(true);
    }

    private void openValueTreatmentDialog() {
        ImportValueTreatmentDialog dialog = new ImportValueTreatmentDialog(mainFrame);
        dialog.setVisible(true);
    }


    private void openExpensesEditForm() {
        try {
            List<Expense> expenses = ExpensesConverter.transformImportData(sharedData);

            mainFrame.registerAndShowScreen(
                    ScreenNames.EXPENSES_EDIT_FORM,
                    new ExpensesEditForm(mainFrame, expenses, budget, returnScreen, onImportSuccess)
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
