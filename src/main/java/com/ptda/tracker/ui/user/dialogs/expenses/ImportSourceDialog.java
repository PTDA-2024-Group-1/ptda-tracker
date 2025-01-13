package com.ptda.tracker.ui.user.dialogs.expenses;

import com.ptda.tracker.util.ExpensesImportSharedData;
import com.ptda.tracker.util.LocaleManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ImportSourceDialog extends JDialog {
    private final ExpensesImportSharedData sharedData;
    private List<String[]> rawData;
    private boolean hasHeader;
    private final Runnable onDone;
    private String[] originalFirstRow;

    public ImportSourceDialog(JFrame parent, Runnable onDone) {
        super(parent, IMPORT_SOURCE, true);
        this.onDone = onDone;
        this.sharedData = ExpensesImportSharedData.getInstance();
        hasHeader = true;

        initComponents();
        setListeners();
    }

    private void setListeners() {
        skipButton.addActionListener(e -> dispose());
        fileButton.addActionListener(e -> importFromFile());
        clipboardButton.addActionListener(e -> importFromClipboard());
        confirmButton.addActionListener(e -> {
            sharedData.setRawData(rawData);
            sharedData.setHasHeader(hasHeader);
            dispose();
            onDone.run();
        });
    }

    private void importFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                rawData = new ArrayList<>();
                while ((line = br.readLine()) != null) {
                    rawData.add(line.split(","));
                }
                processRawData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        this,
                        FAILED_TO_IMPORT_DATA,
                        ERROR,
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void importFromClipboard() {
        try {
            String clipboardData = (String) Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .getData(DataFlavor.stringFlavor);
            String[] lines = clipboardData.split("\n");
            rawData = new ArrayList<>();
            for (String line : lines) {
                if (line.contains("\t")) {
                    rawData.add(line.split("\t"));
                } else {
                    rawData.add(line.split(","));
                }
            }
            processRawData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    FAILED_TO_IMPORT_DATA,
                    ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void processRawData() {
        if (rawData == null || rawData.isEmpty()) {
            return;
        }

        String[] columnNames;
        String[][] data;

        if (hasHeader) {
            if (originalFirstRow == null) {
                originalFirstRow = rawData.getFirst();
            }
            columnNames = rawData.getFirst();
            data = new String[rawData.size() - 1][];
            for (int i = 1; i < rawData.size(); i++) {
                data[i - 1] = rawData.get(i);
            }
        } else {
            int columnCount = rawData.getFirst().length;
            columnNames = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                columnNames[i] = COLUMN + " " + (i + 1);
            }
            data = new String[rawData.size()][];
            for (int i = 0; i < rawData.size(); i++) {
                data[i] = rawData.get(i);
            }
        }

        createTablePreview(data, columnNames);

        confirmButton.setEnabled(true);
        pack();
        setLocationRelativeTo(null);
    }

    private void createTablePreview(String[][] data, String[] columnNames) {
        // Create a checkbox to toggle the header
        JCheckBox hasHeaderCheckBox = new JCheckBox(FIRST_ROW_CONTAINS_COLUMN_NAMES);
        hasHeaderCheckBox.setSelected(hasHeader);
        hasHeaderCheckBox.addActionListener(e -> {
            hasHeader = hasHeaderCheckBox.isSelected();
            processRawData();
        });

        // Create a table to preview the data
        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        previewTablePanel.removeAll();
        previewTablePanel.setLayout(new BorderLayout());
        previewTablePanel.add(hasHeaderCheckBox, BorderLayout.NORTH);
        previewTablePanel.add(scrollPane, BorderLayout.CENTER);
        previewTablePanel.revalidate();
        previewTablePanel.repaint();
    }

    private void initComponents() {
        // Initialize components
        previewTablePanel = new JPanel(new BorderLayout());
        skipButton = new JButton(SKIP);
        fileButton = new JButton(IMPORT_FROM_FILE);
        clipboardButton = new JButton(IMPORT_FROM_CLIPBOARD);
        confirmButton = new JButton(CONFIRM);
        confirmButton.setEnabled(false);

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(skipButton);
        buttonPanel.add(fileButton);
        buttonPanel.add(clipboardButton);
        buttonPanel.add(confirmButton);

        // Set layout for the content pane
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(previewTablePanel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        // Set dialog properties
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel previewTablePanel;
    private JButton skipButton, fileButton, clipboardButton, confirmButton;
    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            IMPORT_SOURCE = localeManager.getTranslation("import.source"),
            SKIP = localeManager.getTranslation("skip"),
            IMPORT_FROM_FILE = localeManager.getTranslation("import.from.file"),
            IMPORT_FROM_CLIPBOARD = localeManager.getTranslation("import.from.clipboard"),
            CONFIRM = localeManager.getTranslation("confirm"),
            COLUMN = localeManager.getTranslation("column"),
            FIRST_ROW_CONTAINS_COLUMN_NAMES = localeManager.getTranslation("row.contains.column"),
            FAILED_TO_IMPORT_DATA = localeManager.getTranslation("failed.import"),
            ERROR = localeManager.getTranslation("error");
}
