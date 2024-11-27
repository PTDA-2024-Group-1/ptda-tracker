package com.ptda.tracker.ui.dialogs;

import com.ptda.tracker.models.dispute.Subdivision;

import javax.swing.*;
import java.util.List;

public class SubdivisionsDialog extends JDialog {
    private final List<Subdivision> subdivisions;
    private JTable subdivisionsTable;

    public SubdivisionsDialog(List<Subdivision> subdivisions) {
        this.subdivisions = subdivisions;
        initUI();
    }

    private void initUI() {
        setTitle("Subdivisions");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        subdivisionsTable = createSubdivisionsTable(subdivisions);
        JScrollPane scrollPane = new JScrollPane(subdivisionsTable);
        add(scrollPane);
    }

    private JTable createSubdivisionsTable(List<Subdivision> subdivisions) {
        return createSubdivisionsJTable(subdivisions);
    }

    public static JTable createSubdivisionsJTable(List<Subdivision> subdivisions) {
        String[] columnNames = {"ID", "Amount", "Percentage", "Created By"};
        Object[][] data = new Object[subdivisions.size()][columnNames.length];
        for (int i = 0; i < subdivisions.size(); i++) {
            Subdivision subdivision = subdivisions.get(i);
            data[i][0] = subdivision.getId();
            data[i][1] = subdivision.getAmount() + "€";
            data[i][2] = subdivision.getPercentage() + "%";
            data[i][3] = subdivision.getCreatedBy().getName();
        }
        return new JTable(data, columnNames);
    }
}
