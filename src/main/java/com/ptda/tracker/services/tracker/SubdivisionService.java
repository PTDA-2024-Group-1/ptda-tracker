package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.dispute.Subdivision;
import java.util.List;

public interface SubdivisionService {
    Subdivision create(Subdivision subdivision);
    Subdivision update(Subdivision subdivision);
    boolean delete(Long id);
    Subdivision getById(Long id);
    List<Subdivision> getAllByExpenseId(Long expenseId);
    Subdivision save(Subdivision subdivision);
}