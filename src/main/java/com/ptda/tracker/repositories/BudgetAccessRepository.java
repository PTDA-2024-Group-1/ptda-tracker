package com.ptda.tracker.repositories;

import com.ptda.tracker.models.tracker.BudgetAccess;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetAccessRepository extends JpaRepository<BudgetAccess, Long> {

    List<BudgetAccess> findAllByUserId(Long userId);

    List<BudgetAccess> findAllByBudgetId(Long budgetId);

    Optional<BudgetAccess> findByBudgetIdAndUserId(Long budgetId, Long userId);

    List<BudgetAccess> deleteAllByUserId(Long userId);

    boolean existsByBudgetIdAndUserId(Long budgetId, Long userId);

    List<BudgetAccess> findAllByUserIdOrderByBudgetUpdatedAtDesc(Long userId, Pageable pageable);

    int countByUserId(Long userId);

    int deleteByBudgetIdAndUserId(Long id, Long userId);

}
