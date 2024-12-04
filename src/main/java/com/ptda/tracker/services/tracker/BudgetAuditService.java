package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.user.User;
import jakarta.transaction.Transactional;

import java.util.List;

public interface BudgetAuditService {

    @Transactional
    List<Object[]> getBudgetRevisionsWithDetails(Long budgetId);

    @Transactional
    List<Number> getBudgetRevisions(Long budgetId);

    @Transactional
    Budget getBudgetAtRevision(Long budgetId, Number revision);

    @Transactional
    boolean hasNameOrDescriptionChanged(Long budgetId, Number revision);
}
