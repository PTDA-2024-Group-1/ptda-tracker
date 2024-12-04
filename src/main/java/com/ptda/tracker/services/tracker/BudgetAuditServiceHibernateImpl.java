package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.Budget;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetAuditServiceHibernateImpl implements BudgetAuditService {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getBudgetRevisionsWithDetails(Long budgetId) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        return auditReader.createQuery()
                .forRevisionsOfEntity(Budget.class, false, true)
                .add(AuditEntity.id().eq(budgetId))
                .getResultList();
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

    public boolean hasNameOrDescriptionChanged(Long budgetId, Number revision) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        Budget currentRevision = auditReader.find(Budget.class, budgetId, revision);
        Budget previousRevision = auditReader.find(Budget.class, budgetId, revision.intValue() - 1);

        if (previousRevision == null) {
            return false;
        }

        return !currentRevision.getName().equals(previousRevision.getName()) ||
                !currentRevision.getDescription().equals(previousRevision.getDescription());
    }
}