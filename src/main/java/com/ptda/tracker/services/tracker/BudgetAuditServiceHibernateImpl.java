package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.Budget;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetAuditServiceHibernateImpl implements BudgetAuditService {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public List<Object[]> getBudgetRevisionsWithDetails(Long budgetId) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        AuditQuery query = auditReader.createQuery()
                .forRevisionsOfEntity(Budget.class, false, true)
                .add(AuditEntity.id().eq(budgetId));
        return query.getResultList();
    }

    @Override
    public List<Number> getBudgetRevisions(Long budgetId) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        return auditReader.getRevisions(Budget.class, budgetId);
    }

    @Override
    public Budget getBudgetAtRevision(Long budgetId, Number revision) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        return auditReader.find(Budget.class, budgetId, revision);
    }
}
