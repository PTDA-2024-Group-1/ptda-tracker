package com.ptda.tracker.ui.user.dialogs;

import com.ptda.tracker.models.tracker.Subdivision;

import javax.swing.*;
import java.util.List;

public class SubdivisionsDialog extends JDialog {
    private final List<Subdivision> subdivisions;

    public SubdivisionsDialog(List<Subdivision> subdivisions) {
        this.subdivisions = subdivisions;
        initUI();
    }

    private void initUI() {
        setTitle(TITLE);
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
        String[] columnNames = {ID, AMOUNT, PERCENTAGE, CREATED_BY, ASSOCIATED_USER};
        Object[][] data = new Object[subdivisions.size()][columnNames.length];
        for (int i = 0; i < subdivisions.size(); i++) {
            Subdivision subdivision = subdivisions.get(i);
            data[i][0] = subdivision.getId();
            data[i][1] = subdivision.getAmount() + "€";
            data[i][2] = subdivision.getPercentage() + "%";
            data[i][3] = subdivision.getCreatedBy().getName();
            data[i][4] = subdivision.getUser().getName();
        }
        return new JTable(data, columnNames);
    }

    private JTable subdivisionsTable;
    private static final String
            TITLE = "Subdivisions",
            ID = "ID",
            AMOUNT = "Amount",
            PERCENTAGE = "Percentage",
            CREATED_BY = "Created By",
            ASSOCIATED_USER = "Associated User";
}