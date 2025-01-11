package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.BudgetSplit;

import java.util.List;

public interface BudgetSplitService {

    List<BudgetSplit> getAllByBudgetId(Long budgetId);

    List<BudgetSplit> split(Long budgetId);

}
