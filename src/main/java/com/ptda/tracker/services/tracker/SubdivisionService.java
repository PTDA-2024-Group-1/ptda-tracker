package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.ExpenseDivision;

import java.util.List;

public interface SubdivisionService {

    ExpenseDivision create(ExpenseDivision expenseDivision);

    List<ExpenseDivision> create(List<ExpenseDivision> expenseDivisions);

    ExpenseDivision update(ExpenseDivision expenseDivision);

    boolean delete(Long id);

    ExpenseDivision getById(Long id);

    List<ExpenseDivision> getAllByExpenseId(Long expenseId);

}