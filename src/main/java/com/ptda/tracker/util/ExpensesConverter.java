package com.ptda.tracker.util;

import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.tracker.ExpenseCategory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExpensesConverter {
    public static List<Expense> transformImportData(ExpensesImportSharedData sharedData) {
        List<Expense> expenses = new ArrayList<>();

        // Retrieve all relevant data from ExpensesImportSharedData
        List<String[]> rawData = sharedData.getRawData();
        Map<String, Integer> columnMapping = sharedData.getColumnMapping();
        Map<String, ExpenseCategory> categoryMapping = sharedData.getCategoryMapping();
        String dateFormat = sharedData.getDateFormat();
        boolean hasHeader = sharedData.isHasHeader();
        String valueTreatment = sharedData.getValueTreatment();

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

        int startIndex = hasHeader ? 1 : 0; // Skip the header row if present

        for (int i = startIndex; i < rawData.size(); i++) {
            String[] row = rawData.get(i);

            // Filter out rows that don't meet the inclusion criteria
            if (!shouldIncludeRow(row, columnMapping, valueTreatment)) {
                continue;
            }

            Expense expense = new Expense();
            for (Map.Entry<String, Integer> entry : columnMapping.entrySet()) {
                String mappedField = entry.getKey(); // e.g., "AMOUNT", "DATE"
                int columnIndex = entry.getValue(); // Index in raw data

                if (columnIndex >= row.length) {
                    System.err.println("Warning: Column index " + columnIndex + " out of bounds for row " + i);
                    continue;
                }

                String cellValue = row[columnIndex];
                try {
                    switch (mappedField) {
                        case "AMOUNT":
                            String sanitizedValue = cellValue.replace(" ", "").replace(",", ".");
                            double amount = Double.parseDouble(sanitizedValue);
                            expense.setAmount(makePositive(amount, valueTreatment)); // Apply absolute transformation if needed
                            break;
                        case "DATE":
                            if (cellValue != null && !cellValue.trim().isEmpty()) {
                                expense.setDate(sdf.parse(cellValue));
                            }
                            break;
                        case "CATEGORY":
                            if (cellValue == null || cellValue.trim().isEmpty()) {
                                expense.setCategory(ExpenseCategory.OTHER); // Set to OTHER if the category is empty
                            } else {
                                ExpenseCategory category = categoryMapping != null ? categoryMapping.get(cellValue) : null;
                                expense.setCategory(category != null ? category : ExpenseCategory.OTHER);
                            }
                            break;
                        case "TITLE":
                            expense.setTitle(cellValue);
                            break;
                        case "DESCRIPTION":
                            expense.setDescription(cellValue);
                            break;
                        default:
                            System.err.println("Warning: Unhandled mapping field " + mappedField);
                            break;
                    }
                } catch (ParseException e) {
                    System.err.println("Error parsing field: " + mappedField + " with value: " + cellValue + " at row " + i);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing amount: " + cellValue + " at row " + i);
                }
            }

            expenses.add(expense);
        }

        return expenses;
    }

    private static boolean shouldIncludeRow(String[] row, Map<String, Integer> columnMapping, String valueTreatment) {
        if (columnMapping == null) {
            return true; // Include all rows if no column mapping is set
        }

        Integer amountColumnIndex = columnMapping.get("AMOUNT");
        if (amountColumnIndex == null || amountColumnIndex >= row.length) {
            return true; // Include rows if no "AMOUNT" column is mapped
        }

        try {
            double amount = Double.parseDouble(row[amountColumnIndex].replace(",", "."));

            return switch (valueTreatment) {
                case "NEGATIVE_AS_EXPENSE" -> amount < 0; // Include only negative amounts
                case "POSITIVE_AS_EXPENSE" -> amount > 0; // Include only positive amounts
                case "ABSOLUTE_VALUE" -> true; // Include all values
                default -> true; // Default to include all rows
            };
        } catch (NumberFormatException e) {
            System.err.println("Invalid amount value: " + row[amountColumnIndex]);
            return false; // Exclude rows with invalid amounts
        }
    }

    private static double makePositive(double amount, String valueTreatment) {
        return switch (valueTreatment) {
            case "NEGATIVE_AS_EXPENSE" -> Math.abs(amount); // Convert negative values to positive
            case "POSITIVE_AS_EXPENSE" -> Math.abs(amount); // Convert positive values to positive
            case "ABSOLUTE_VALUE" -> Math.abs(amount); // Convert all values to positive
            default -> amount; // Return as-is if no specific treatment
        };
    }

}
