package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.Budget;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * The BudgetAuditService interface defines methods for auditing budgets, including
 * retrieving budget revisions with details, obtaining specific revisions, and checking
 * if the name or description of a budget has changed.
 */
public interface BudgetAuditService {

    @Transactional(Transactional.TxType.SUPPORTS) // Read-only transaction
    List<Object[]> getBudgetRevisionsWithDetails(Long budgetId);

    @Transactional(Transactional.TxType.SUPPORTS) // Read-only transaction
    List<Number> getBudgetRevisions(Long budgetId);

    @Transactional(Transactional.TxType.SUPPORTS) // Read-only transaction
    Budget getBudgetAtRevision(Long budgetId, Number revision);

    @Transactional(Transactional.TxType.SUPPORTS) // Read-only transaction
    boolean hasNameOrDescriptionChanged(Long budgetId, Number revision);

    /**
     * New method to retrieve the budget entity at a specific revision using a Long revision number.
     * This can delegate to the existing {@code getBudgetAtRevision(Long, Number)} implementation internally.
     */
    @Transactional
    default Budget getBudgetAtRevision(Long budgetId, Long revisionNumber) {
        return getBudgetAtRevision(budgetId, (Number) revisionNumber);
    }
}
