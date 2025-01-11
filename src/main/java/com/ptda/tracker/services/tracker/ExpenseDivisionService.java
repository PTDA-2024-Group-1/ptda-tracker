package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.ExpenseDivision;

import java.util.List;

public interface ExpenseDivisionService {

    List<ExpenseDivision> getAllByExpenseId(Long expenseId);

    ExpenseDivision getById(Long id);

    ExpenseDivision create(ExpenseDivision expenseDivision);

    List<ExpenseDivision> createAll(List<ExpenseDivision> expenseDivisions);

    ExpenseDivision update(ExpenseDivision expenseDivision);

    List<ExpenseDivision> updateAll(List<ExpenseDivision> expenseDivisions);

    boolean deleteById(Long id);

    boolean deleteAllByExpenseId(Long id);

}