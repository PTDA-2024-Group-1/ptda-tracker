package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.Budget;

import java.util.List;

public interface BudgetAuditService {

    List<Object[]> getBudgetRevisionsWithDetails(Long budgetId);

    List<Number> getBudgetRevisions(Long budgetId);

    Budget getBudgetAtRevision(Long budgetId, Number revision);

}
