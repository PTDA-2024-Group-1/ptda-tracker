package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.Budget;
import jakarta.transaction.Transactional;
import org.hibernate.envers.DefaultRevisionEntity;

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

    /**
     * New method to retrieve the budget entity at a specific revision using a Long revision number.
     * This can delegate to the existing {@code getBudgetAtRevision(Long, Number)} implementation internally.
     */
    @Transactional
    default Budget getBudgetAtRevision(Long budgetId, Long revisionNumber) {
        return getBudgetAtRevision(budgetId, (Number) revisionNumber);
    }

    @Transactional
    DefaultRevisionEntity getRevisionEntity(long revisionNumber);

}
