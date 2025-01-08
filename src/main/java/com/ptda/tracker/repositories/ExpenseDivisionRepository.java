package com.ptda.tracker.repositories;

import com.ptda.tracker.models.tracker.ExpenseDivision;
import com.ptda.tracker.models.tracker.ExpenseDivisionState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseDivisionRepository extends JpaRepository<ExpenseDivision, Long> {

    List<ExpenseDivision> findAllByExpenseId(Long expenseId);

    void deleteByExpenseId(Long expenseId);

    List<ExpenseDivision> findAllByExpenseIdAndState(Long expenseId, ExpenseDivisionState state);

}