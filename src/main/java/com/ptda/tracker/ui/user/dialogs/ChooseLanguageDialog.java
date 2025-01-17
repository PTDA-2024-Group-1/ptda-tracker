package com.ptda.tracker.ui.user.dialogs;

import com.ptda.tracker.TrackerApplication;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.LocaleManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.prefs.Preferences;

public class ChooseLanguageDialog extends JDialog {
    private final MainFrame mainFrame;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox<String> languageComboBox;
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
        setTitle(CHOOSE_LANGUAGE);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel label = new JLabel(SELECT_YOUR_PREFERRED_LANGUAGE);
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
        buttonOK = new JButton(OK);
        buttonCancel = new JButton(CANCEL);
        buttonOK.setToolTipText(CONFIRM_YOUR_LANGUAGE_CHOICE);
        buttonCancel.setToolTipText(CANCEL_AND_CLOSE_THE_DIALOG);
        buttonOK.addActionListener(e -> {
            try {
                handleOkAction();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        buttonCancel.addActionListener(e -> dispose());
        buttonPanel.add(buttonCancel);
        buttonPanel.add(buttonOK);

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        contentPane.add(buttonPanel, gbc);

        pack();
        setLocationRelativeTo(mainFrame);
    }

    private void handleOkAction() throws IOException {
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
                YOU_NEED_TO_RESTART_THE_APPLICATION_FOR_THE_CHANGES_TO_TAKE_EFFECT_RESTART_NOW,
                RESTART_APPLICATION,
                JOptionPane.YES_NO_OPTION
        );
        if (result == JOptionPane.YES_OPTION) {
            System.out.println("Application is restarting...");
            String javaBin = System.getProperty("java.home") + "/bin/java";
            String classPath = System.getProperty("java.class.path");
            String className = TrackerApplication.class.getName();
            ProcessBuilder processBuilder = new ProcessBuilder(javaBin, "-cp", classPath, className);
            processBuilder.start();
            System.exit(0);
        } else {
            dispose();
        }
    }
    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            CHOOSE_LANGUAGE = localeManager.getTranslation("choose_language"),
            SELECT_YOUR_PREFERRED_LANGUAGE = localeManager.getTranslation("select_your_preferred_language"),
            OK = localeManager.getTranslation("ok"),
            CANCEL = localeManager.getTranslation("cancel"),
            CONFIRM_YOUR_LANGUAGE_CHOICE = localeManager.getTranslation("confirm_your_language_choice"),
            CANCEL_AND_CLOSE_THE_DIALOG = localeManager.getTranslation("cancel_and_close_the_dialog"),
            YOU_NEED_TO_RESTART_THE_APPLICATION_FOR_THE_CHANGES_TO_TAKE_EFFECT_RESTART_NOW = localeManager.getTranslation("you_need_to_restart_the_application_for_the_changes_to_take_effect_restart_now"),
            RESTART_APPLICATION = localeManager.getTranslation("restart_application");

}
