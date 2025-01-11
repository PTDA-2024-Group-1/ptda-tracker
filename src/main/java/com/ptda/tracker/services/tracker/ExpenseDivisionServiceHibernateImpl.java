package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.ExpenseDivision;
import com.ptda.tracker.repositories.ExpenseDivisionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExpenseDivisionServiceHibernateImpl implements ExpenseDivisionService {

    private final ExpenseDivisionRepository expenseDivisionRepository;

    @Override
    public List<ExpenseDivision> getAllByExpenseId(Long expenseId) {
        return expenseDivisionRepository.findAllByExpenseId(expenseId);
    }

    @Override
    public ExpenseDivision getById(Long id) {
        Optional<ExpenseDivision> optionalSubdivision = expenseDivisionRepository.findById(id);
        return optionalSubdivision.orElse(null);
    }

    @Override
    @Transactional
    public ExpenseDivision create(ExpenseDivision expenseDivision) {
        return expenseDivisionRepository.save(expenseDivision);
    }

    @Override
    @Transactional
    public List<ExpenseDivision> createAll(List<ExpenseDivision> expenseDivisions) {
        return expenseDivisionRepository.saveAll(expenseDivisions);
    }

    @Override
    @Transactional
    public ExpenseDivision update(ExpenseDivision expenseDivision) {
        return expenseDivisionRepository.save(expenseDivision);
    }

    @Override
    @Transactional
    public List<ExpenseDivision> updateAll(List<ExpenseDivision> expenseDivisions) {
        return expenseDivisionRepository.saveAll(expenseDivisions);
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        if (expenseDivisionRepository.existsById(id)) {
            expenseDivisionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean deleteAllByExpenseId(Long id) {
        expenseDivisionRepository.deleteAllByExpenseId(id);
        return true;
    }

}