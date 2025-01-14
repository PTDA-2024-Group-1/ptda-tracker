package com.ptda.tracker.util;

import lombok.Getter;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class LocaleManager {
    private static LocaleManager instance;
    @Getter
    private Locale currentLocale;
    private ResourceBundle translations;

    // Private constructor to prevent instantiation
    private LocaleManager() {
    }

    // Public method to provide a single instance
    public static LocaleManager getInstance() {
        if (instance == null) {
            synchronized (LocaleManager.class) {
                if (instance == null) {
                    instance = new LocaleManager();
                }
            }
        }
        return instance;
    }

    // Method to set the locale
    public void setLocale(Locale locale) {
        this.currentLocale = locale;
        this.translations = ResourceBundle.getBundle("messages", currentLocale);
    }

    // Method to get the translation for a given key
    public String getTranslation(String key) {
        try {
            return translations.getString(key);
        } catch (Exception e) {
            return key;
        }
    }

    public Map<String, Locale> getSupportedLocales() {
        Map<String, Locale> locales = new HashMap<>();
        locales.put("English", new Locale("", ""));
        locales.put("Português", new Locale("pt", "PT"));
//        locales.put("Español", new Locale("es", "ES"));
//        locales.put("Français", new Locale("fr", "FR"));
//        locales.put("Deutsch", new Locale("de", "DE"));
//        locales.put("Русский", new Locale("ru", "RU"));
//        locales.put("Italiano", new Locale("it", "IT"));
        return locales;
    }
}
