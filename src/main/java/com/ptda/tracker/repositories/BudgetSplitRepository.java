package com.ptda.tracker.repositories;

import com.ptda.tracker.models.tracker.BudgetSplit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetSplitRepository extends JpaRepository<BudgetSplit, Long> {

    List<BudgetSplit> getAllByBudgetId(Long budgetId);

}
