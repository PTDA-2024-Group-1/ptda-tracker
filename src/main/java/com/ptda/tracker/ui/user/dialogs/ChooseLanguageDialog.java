package com.ptda.tracker.ui.user.dialogs;

import com.ptda.tracker.TrackerApplication;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.LocaleManager;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.Map;
import java.util.prefs.Preferences;

public class ChooseLanguageDialog extends JDialog {
    private final MainFrame mainFrame;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox<String> languageComboBox;
    private LocaleManager localeManager = LocaleManager.getInstance();
    private Locale currentLocale;
    private Map<String, Locale> languages;

    public ChooseLanguageDialog(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setModal(true);

        initComponents();
    }

    private void initComponents() {
        contentPane = new JPanel(new GridBagLayout());
        setContentPane(contentPane);
        setTitle("Choose Language");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel label = new JLabel("Select your preferred language:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPane.add(label, gbc);

        languageComboBox = new JComboBox<>();
        languages = LocaleManager.getInstance().getSupportedLocales();
        languages.forEach((key, value) -> languageComboBox.addItem(key));
        currentLocale = localeManager.getCurrentLocale();
        String currentLanguage = languages.entrySet().stream()
                .filter(entry -> entry.getValue().equals(currentLocale))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("English");
        languageComboBox.setSelectedItem(currentLanguage);
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        contentPane.add(languageComboBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonOK = new JButton("OK");
        buttonCancel = new JButton("Cancel");
        buttonOK.setToolTipText("Confirm your language choice");
        buttonCancel.setToolTipText("Cancel and close the dialog");
        buttonOK.addActionListener(e -> handleOkAction());
        buttonCancel.addActionListener(e -> dispose());
        buttonPanel.add(buttonOK);
        buttonPanel.add(buttonCancel);

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        contentPane.add(buttonPanel, gbc);

        pack();
        setLocationRelativeTo(mainFrame);
    }

    private void handleOkAction() {
        String selectedLanguage = (String) languageComboBox.getSelectedItem();
        Locale selectedLocale = languages.get(selectedLanguage);

        if (selectedLocale.equals(localeManager.getCurrentLocale())) {
            dispose();
            return;
        }

        Preferences preferences = Preferences.userNodeForPackage(TrackerApplication.class);
        preferences.put("language", selectedLocale.getLanguage());
        preferences.put("country", selectedLocale.getCountry());
        Locale locale = new Locale(preferences.get("language", "en"), preferences.get("country", "US"));
        localeManager.setLocale(locale);

        int result = JOptionPane.showConfirmDialog(
                this,
                "You need to restart the application for the changes to take effect. Restart now?",
                "Restart Application",
                JOptionPane.YES_NO_OPTION
        );
        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        } else {
            dispose();
        }
    }
}
