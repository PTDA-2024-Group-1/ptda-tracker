package com.ptda.tracker.util;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.ExpenseCategory;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ExpensesImportSharedData {
    private static ExpensesImportSharedData instance;

    private List<String[]> rawData;
    private Map<String, Integer> columnMapping;
    private Map<String, ExpenseCategory> categoryMapping;
    private String dateFormat;
    private boolean hasHeader;
    private String valueTreatment;
    private Budget defaultBudget;

    public static ExpensesImportSharedData getInstance() {
        if (instance == null) {
            instance = new ExpensesImportSharedData();
        }
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

    public Map<String, ExpenseCategory> getCategoryMapping() {
        if (categoryMapping == null) {
            categoryMapping = new HashMap<>(); // Initialize as mutable map
        }
        return categoryMapping;
    }

    public Map<String, Integer> getColumnMapping() {
        if (columnMapping == null) {
            columnMapping = new HashMap<>(); // Initialize as mutable map
        }
        return columnMapping;
    }
}
