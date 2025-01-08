package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.ExpenseDivision;
import com.ptda.tracker.models.tracker.ExpenseDivisionState;

import java.util.List;

public interface ExpenseDivisionService {

    List<ExpenseDivision> getAllByExpenseId(Long expenseId);

    List<ExpenseDivision> getAllByExpenseIdAndState(Long expenseId, ExpenseDivisionState state);

    ExpenseDivision getById(Long id);

    ExpenseDivision create(ExpenseDivision expenseDivision);

    List<ExpenseDivision> createAll(List<ExpenseDivision> expenseDivisions);

    ExpenseDivision update(ExpenseDivision expenseDivision);

    boolean delete(Long id);

}