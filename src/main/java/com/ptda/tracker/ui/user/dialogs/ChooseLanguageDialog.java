package com.ptda.tracker.ui.user.dialogs;

import com.ptda.tracker.TrackerApplication;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.LocaleManager;

import javax.swing.*;
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
        contentPane = new JPanel();
        setContentPane(contentPane);
        setModal(true);

        initUI();
    }

    private void initUI() {
        setTitle("Choose Language");
        setSize(300, 200);
        setLocationRelativeTo(null);

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

        contentPane.add(languageComboBox);

        buttonCancel = new JButton("Cancel");
        buttonCancel.addActionListener(e -> dispose());
        contentPane.add(buttonCancel);

        buttonOK = new JButton("OK");
        buttonOK.addActionListener(e -> {
            String selectedLanguage = (String) languageComboBox.getSelectedItem();
            Locale selectedLocale = languages.get(selectedLanguage);

            if (selectedLocale == LocaleManager.getInstance().getCurrentLocale()) {
                dispose();
                return;
            }

            Preferences preferences = Preferences.userNodeForPackage(TrackerApplication.class);
            preferences.put("language", selectedLocale.getLanguage());
            preferences.put("country", selectedLocale.getCountry());
            Locale locale = new Locale(preferences.get("language", "en"), preferences.get("country", "US"));
            localeManager.setLocale(locale);

            // Show dialog to restart application
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
        });
        contentPane.add(buttonOK);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
}
