package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.ExpenseColumnMapping;

import java.util.List;

public interface ExpenseColumnMappingService {

    List<ExpenseColumnMapping> getAllByUserId(Long userId);

    ExpenseColumnMapping create(ExpenseColumnMapping expenseColumnMapping);

    ExpenseColumnMapping update(ExpenseColumnMapping expenseColumnMapping);

    void delete(Long id);

}
