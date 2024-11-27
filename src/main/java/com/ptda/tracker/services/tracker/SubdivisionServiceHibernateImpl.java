package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.dispute.Subdivision;
import com.ptda.tracker.repositories.SubdivisionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubdivisionServiceHibernateImpl implements SubdivisionService {

    private final SubdivisionRepository subdivisionRepository;

    @Override
    @Transactional
    public Subdivision create(Subdivision subdivision) {
        return subdivisionRepository.save(subdivision);
    }

    @Override
    @Transactional
    public Subdivision update(Subdivision subdivision) {
        return subdivisionRepository.save(subdivision);
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        if (subdivisionRepository.existsById(id)) {
            subdivisionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Subdivision getById(Long id) {
        Optional<Subdivision> optionalSubdivision = subdivisionRepository.findById(id);
        return optionalSubdivision.orElse(null);
    }

    @Override
    public List<Subdivision> getAllByExpenseId(Long expenseId) {
        return subdivisionRepository.findAllByExpenseId(expenseId);
    }

    @Override
    @Transactional
    public Subdivision save(Subdivision subdivision) { // Implementing the save method
        return subdivisionRepository.save(subdivision);
    }
}