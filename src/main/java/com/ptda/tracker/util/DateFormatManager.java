package com.ptda.tracker.util;

public class DateFormatManager {
    private static DateFormatManager instance;
    private String dateFormat;

    private DateFormatManager() {
        // Private constructor to prevent instantiation
    }

    public static DateFormatManager getInstance() {
        if (instance == null) {
            instance = new DateFormatManager();
        }
        return instance;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}
