package com.ptda.tracker.repositories;

import com.ptda.tracker.models.tracker.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findAllByCreatedById(Long userId);

}
