package com.ptda.tracker.util;

import com.ptda.tracker.models.tracker.ExpenseCategory;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Data
public class ImportSharedData {
    private static ImportSharedData instance;

    private List<String[]> rawData;
    private Map<String, Integer> columnMapping;
    private Map<String, ExpenseCategory> categoryMapping;
    private String dateFormat;
    @Getter
    private boolean hasHeader;

    public static ImportSharedData getInstance() {
        if (instance == null) {
            instance = new ImportSharedData();
        }
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }
}
