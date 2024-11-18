package com.ptda.tracker.repositories;

import com.ptda.tracker.models.tracker.BudgetAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetAccessRepository extends JpaRepository<BudgetAccess, Long> {

    List<BudgetAccess> findAllByUserId(Long userId);

    List<BudgetAccess> findAllByBudgetId(Long budgetId);

}
