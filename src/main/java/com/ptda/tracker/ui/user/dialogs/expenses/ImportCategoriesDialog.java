package com.ptda.tracker.ui.user.dialogs.expenses;

import com.ptda.tracker.util.ImportSharedData;

import javax.swing.*;

public class ImportCategoriesDialog extends JDialog {
    private ImportSharedData sharedData;

    public ImportCategoriesDialog(JFrame parent) {
        super(parent, "Import Categories", true);
        this.sharedData = ImportSharedData.getInstance();
    }
}
