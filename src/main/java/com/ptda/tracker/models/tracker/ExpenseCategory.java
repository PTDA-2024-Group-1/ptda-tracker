package com.ptda.tracker.models.tracker;

import com.ptda.tracker.util.LocaleManager;

public enum ExpenseCategory {
    FOOD,
    CLOTHES,
    ENTERTAINMENT,
    EDUCATION,
    TRANSPORT,
    HEALTH,
    GIFTS,
    TRAVEL,
    CHILDCARE,
    PERSONAL_CARE,
    TECH,
    EVENTS,
    PET_CARE,
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
            case HEALTH:
                return localeManager.getTranslation("health");
            case GIFTS:
                return localeManager.getTranslation("gifts");
            case TECH:
                return localeManager.getTranslation("tech");
            case EVENTS:
                return localeManager.getTranslation("events");
            case TRAVEL:
                return localeManager.getTranslation("travel");
            case CHILDCARE:
                return localeManager.getTranslation("childcare");
            case PERSONAL_CARE:
                return localeManager.getTranslation("personal_care");
            case PET_CARE:
                return localeManager.getTranslation("pet_care");
            case OTHER:
                return localeManager.getTranslation("other");
            default:
                return this.name();
        }
    }
}