package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.BudgetAccess;
import com.ptda.tracker.models.tracker.BudgetAccessLevel;

import java.util.List;

public interface BudgetAccessService {

    List<BudgetAccess> getAllByBudgetId(Long budgetId);

    List<BudgetAccess> getAllByUserId(Long userId);

    boolean hasAccess(Long budgetId, Long userId, BudgetAccessLevel accessLevel);

    boolean hasAccess(Long budgetId, String userEmail, BudgetAccessLevel accessLevel);

    BudgetAccess create(Long budgetId, Long userId, BudgetAccessLevel accessLevel);

    BudgetAccess create(Long budgetId, String userEmail, BudgetAccessLevel accessLevel);

    BudgetAccess update(BudgetAccess access);

    boolean delete(Long accessId);

    boolean deleteAllByUserId(Long userId);

}
