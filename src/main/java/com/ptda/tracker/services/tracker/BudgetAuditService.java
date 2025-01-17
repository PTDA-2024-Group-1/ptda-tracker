package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.Budget;
import jakarta.transaction.Transactional;
import org.hibernate.envers.DefaultRevisionEntity;

import java.util.List;

public interface BudgetAuditService {

    List<Object[]> getBudgetRevisionsWithDetails(Long budgetId);

    List<Number> getBudgetRevisions(Long budgetId);

    Budget getBudgetAtRevision(Long budgetId, Number revision);

    boolean hasNameOrDescriptionChanged(Long budgetId, Number revision);

    DefaultRevisionEntity getRevisionEntity(long revisionNumber);

}
