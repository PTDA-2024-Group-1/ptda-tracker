package com.ptda.tracker.repositories;

import com.ptda.tracker.models.dispute.Subdivision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubdivisionRepository extends JpaRepository<Subdivision, Long> {
    List<Subdivision> findAllByExpenseId(Long expenseId);
}