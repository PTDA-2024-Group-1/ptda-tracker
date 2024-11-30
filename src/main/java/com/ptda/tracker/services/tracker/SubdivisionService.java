package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.Subdivision;
import java.util.List;

public interface SubdivisionService {

    Subdivision create(Subdivision subdivision);

    List<Subdivision> create(List<Subdivision> subdivisions);

    Subdivision update(Subdivision subdivision);

    boolean delete(Long id);

    Subdivision getById(Long id);

    List<Subdivision> getAllByExpenseId(Long expenseId);

}