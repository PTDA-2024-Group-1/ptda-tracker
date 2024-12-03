package com.ptda.tracker.repositories;

import com.ptda.tracker.models.tracker.dispute.Dispute;
import com.ptda.tracker.models.tracker.dispute.DisputeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisputeRepository extends JpaRepository<Dispute, Long> {

    List<Dispute> findByExpense_BudgetId(Long budgetId);

    List<Dispute> findByExpense_BudgetIdAndDisputeStatus(Long budgetId, DisputeStatus disputeStatus);

}
