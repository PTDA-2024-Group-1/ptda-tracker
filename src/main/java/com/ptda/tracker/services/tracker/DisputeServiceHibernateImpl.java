package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.dispute.Dispute;
import com.ptda.tracker.models.tracker.dispute.DisputeStatus;
import com.ptda.tracker.repositories.DisputeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DisputeServiceHibernateImpl implements DisputeService {

    private final DisputeRepository disputeRepository;

    @Override
    public List<Dispute> getAllByBudgetId(Long budgetId) {
        return disputeRepository.findByExpense_BudgetId(budgetId);
    }

    @Override
    public List<Dispute> getAllActiveByBudgetIdAndStatus(Long budgetId, DisputeStatus disputeStatus) {
        return disputeRepository.findByExpense_BudgetIdAndDisputeStatus(budgetId, disputeStatus);
    }

    @Override
    public Dispute create(Dispute dispute) {
        return disputeRepository.save(dispute);
    }

    @Override
    public List<Dispute> create(List<Dispute> disputes) {
        return disputeRepository.saveAll(disputes);
    }

    @Override
    public Dispute update(Dispute dispute) {
        return disputeRepository.save(dispute);
    }

    @Override
    public Dispute updateStatus(Long id, DisputeStatus disputeStatus) {
        Dispute dispute = disputeRepository.findById(id).orElse(null);
        if (dispute != null) {
            dispute.setDisputeStatus(disputeStatus);
            return disputeRepository.save(dispute);
        }
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        disputeRepository.deleteById(id);
        return !disputeRepository.existsById(id);
    }
}
