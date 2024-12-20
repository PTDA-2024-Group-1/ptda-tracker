package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.ExpenseColumnMapping;
import com.ptda.tracker.repositories.ExpenseColumnMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseColumnMappingServiceHibernateImpl implements ExpenseColumnMappingService {
    private final ExpenseColumnMappingRepository expenseColumnMappingRepository;

    @Override
    public List<ExpenseColumnMapping> getAllByUserId(Long userId) {
        return expenseColumnMappingRepository.findAllByCreatedBy_Id(userId);
    }

    @Override
    public ExpenseColumnMapping create(ExpenseColumnMapping expenseColumnMapping) {
        return expenseColumnMappingRepository.save(expenseColumnMapping);
    }

    @Override
    public ExpenseColumnMapping update(ExpenseColumnMapping expenseColumnMapping) {
        return expenseColumnMappingRepository.save(expenseColumnMapping);
    }

    @Override
    public void delete(Long id) {
        expenseColumnMappingRepository.deleteById(id);
    }
}
