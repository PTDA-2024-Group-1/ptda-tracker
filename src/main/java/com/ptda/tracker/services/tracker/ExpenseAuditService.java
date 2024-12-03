package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.Expense;

import java.util.List;

public interface ExpenseAuditService {

    List<Object[]> getExpenseRevisionsWithDetails(Long expenseId);

    List<Number> getExpenseRevisions(Long expenseId);

    Expense getExpenseAtRevision(Long expenseId, Number revision);

}
