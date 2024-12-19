package com.ptda.tracker.ui.user.dialogs.expenses;

import com.ptda.tracker.util.ImportSharedData;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

public class ImportCategoriesDialog extends JDialog {
    private ImportSharedData sharedData;

    public ImportCategoriesDialog(JFrame parent, Runnable onDone) {
        super(parent, "Import Categories", true);
        this.sharedData = ImportSharedData.getInstance();
    }
}
