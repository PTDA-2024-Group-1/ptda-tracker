package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.Budget;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * The BudgetAuditService interface defines methods for auditing budgets, including
 * retrieving budget revisions with details, obtaining specific revisions, and checking
 * if the name or description of a budget has changed.
 *
 * All methods are marked as read-only transactions to prevent unintended persistence.
 */
public interface BudgetAuditService {

    /**
     * Retrieves detailed information about all revisions of a specific budget.
     *
     * @param budgetId The ID of the budget.
     * @return A list of Object arrays containing budget revision details.
     */
    @Transactional(Transactional.TxType.SUPPORTS) // Read-only transaction
    List<Object[]> getBudgetRevisionsWithDetails(Long budgetId);

    /**
     * Retrieves all revision numbers for a specific budget.
     *
     * @param budgetId The ID of the budget.
     * @return A list of revision numbers.
     */
    @Transactional(Transactional.TxType.SUPPORTS) // Read-only transaction
    List<Number> getBudgetRevisions(Long budgetId);

    /**
     * Retrieves the budget entity at a specific revision.
     *
     * @param budgetId The ID of the budget.
     * @param revision The revision number.
     * @return The Budget entity at the specified revision, or null if not found.
     */
    @Transactional(Transactional.TxType.SUPPORTS) // Read-only transaction
    Budget getBudgetAtRevision(Long budgetId, Number revision);

    /**
     * Checks if the name or description of the budget has changed at a specific revision.
     *
     * @param budgetId The ID of the budget.
     * @param revision The revision number.
     * @return True if the name or description has changed; otherwise, false.
     */
    @Transactional(Transactional.TxType.SUPPORTS) // Read-only transaction
    boolean hasNameOrDescriptionChanged(Long budgetId, Number revision);

    /**
     * Retrieves the budget entity at a specific revision using a Long revision number.
     * This delegates to the existing {@code getBudgetAtRevision(Long, Number)} implementation.
     *
     * @param budgetId       The ID of the budget.
     * @param revisionNumber The revision number as a Long.
     * @return The Budget entity at the specified revision, or null if not found.
     */
    @Transactional(Transactional.TxType.SUPPORTS) // Read-only transaction
    default Budget getBudgetAtRevision(Long budgetId, Long revisionNumber) {
        return getBudgetAtRevision(budgetId, (Number) revisionNumber);
    }
}
