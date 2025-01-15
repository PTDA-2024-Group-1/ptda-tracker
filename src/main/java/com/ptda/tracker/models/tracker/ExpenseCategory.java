package com.ptda.tracker.models.tracker;

import com.ptda.tracker.util.LocaleManager;

public enum ExpenseCategory {
    FOOD,
    CLOTHES,
    ENTERTAINMENT,
    EDUCATION,
    TRANSPORT,
    OTHER;

    @Override
    public String toString() {
        LocaleManager localeManager = LocaleManager.getInstance();
        switch (this) {
            case FOOD:
                return localeManager.getTranslation("food");
            case CLOTHES:
                return localeManager.getTranslation("clothes");
            case ENTERTAINMENT:
                return localeManager.getTranslation("entertainment");
            case EDUCATION:
                return localeManager.getTranslation("education");
            case TRANSPORT:
                return localeManager.getTranslation("transport");
            case OTHER:
                return localeManager.getTranslation("other");
            default:
                return this.name();
        }
    }
}