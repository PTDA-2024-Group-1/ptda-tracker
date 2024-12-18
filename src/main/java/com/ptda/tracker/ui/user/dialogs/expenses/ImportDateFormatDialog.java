package com.ptda.tracker.ui.user.dialogs.expenses;

import com.ptda.tracker.util.ImportSharedData;

import javax.swing.*;

public class ImportDateFormatDialog extends JDialog {
    private ImportSharedData sharedData;

    public ImportDateFormatDialog(JFrame parent) {
        super(parent, "Import Date Format", true);
        this.sharedData = ImportSharedData.getInstance();
    }
}
