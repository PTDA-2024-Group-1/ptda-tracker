package com.ptda.tracker.dto;

import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.tracker.ExpenseCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseImportDto {
    private String title;
    private String description;
    private String amount;
    private String date;
    private String category;

    public Expense toExpense() {
        Expense expense = new Expense();
        expense.setTitle(title);
        expense.setDescription(description);
        expense.setAmount(Double.parseDouble(amount));
        expense.setDate(java.sql.Date.valueOf(date));
        expense.setCategory(ExpenseCategory.valueOf(category));
        return expense;
    }
}
