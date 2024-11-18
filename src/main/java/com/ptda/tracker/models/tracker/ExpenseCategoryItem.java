package com.ptda.tracker.models.tracker;

public record ExpenseCategoryItem(String localizedName, ExpenseCategory category) {

    @Override
    public String toString() {
        return localizedName;
    }
}