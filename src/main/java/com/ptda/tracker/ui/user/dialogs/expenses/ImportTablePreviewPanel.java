package com.ptda.tracker.ui.user.dialogs.expenses;

import javax.swing.*;
import java.util.List;

public class ImportTablePreviewPanel extends JPanel {
    private List<String[]> rawData;

    public ImportTablePreviewPanel(List<String[]> rawData) {
        this.rawData = rawData;

        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        String[] columnNames = rawData.getFirst();
        String[][] data = new String[rawData.size() - 1][columnNames.length];
        for (int i = 1; i < rawData.size(); i++) {
            data[i - 1] = rawData.get(i);
        }

        expensesTable = new JTable(data, columnNames);
        expensesTable.setFillsViewportHeight(true);
        expensesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        expensesTable.getTableHeader().setReorderingAllowed(false);
        expensesTable.getTableHeader().setResizingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(expensesTable);
        add(scrollPane);
    }

    private JTable expensesTable;
}
