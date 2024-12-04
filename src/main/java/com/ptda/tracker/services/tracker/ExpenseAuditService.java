package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.Expense;
import jakarta.transaction.Transactional;

import java.util.List;

public interface ExpenseAuditService {

    @Transactional
    List<Object[]> getExpenseRevisionsWithDetails(Long expenseId);
    @Transactional
    List<Number> getExpenseRevisions(Long expenseId);
    @Transactional
    Expense getExpenseAtRevision(Long expenseId, Number revision);

}
