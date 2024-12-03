package com.ptda.tracker.services.dispute;

import com.ptda.tracker.models.tracker.dispute.Dispute;
import com.ptda.tracker.models.tracker.dispute.DisputeStatus;

import java.util.List;

public interface DisputeService {

    List<Dispute> getAllByBudgetId(Long budgetId);

    List<Dispute> getAllActiveByBudgetIdAndStatus(Long budgetId, DisputeStatus disputeStatus);

    Dispute create(Dispute dispute);

    List<Dispute> create(List<Dispute> disputes);

    Dispute update(Dispute dispute);

    boolean deleteById(Long id);

}
