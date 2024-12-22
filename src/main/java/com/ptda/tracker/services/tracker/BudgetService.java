package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.Budget;

import java.util.List;
import java.util.Optional;

/**
 * The BudgetService interface defines CRUD operations and additional functionalities
 * for managing Budget entities within the application.
 */
public interface BudgetService {

    /**
     * Retrieves a Budget by its unique identifier.
     *
     * @param id The unique ID of the Budget.
     * @return An Optional containing the Budget if found, otherwise empty.
     */
    Optional<Budget> getById(Long id);

    /**
     * Retrieves all Budgets associated with a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of Budgets belonging to the user.
     */
    List<Budget> getAllByUserId(Long userId);

    /**
     * Retrieves all Budgets in the system.
     *
     * @return A list of all Budgets.
     */
    List<Budget> getAll();

    /**
     * Calculates the total amount allocated across all Budgets for a specific user.
     *
     * @param userId The ID of the user.
     * @return The total budget amount.
     */
    double getTotalBudgetAmount(Long userId);

    /**
     * Creates a new Budget.
     *
     * @param budget The Budget entity to be created.
     * @return The created Budget with an assigned ID.
     */
    Budget create(Budget budget);

    /**
     * Updates an existing Budget.
     *
     * @param budget The Budget entity with updated information.
     * @return The updated Budget.
     */
    Budget update(Budget budget);

    /**
     * Updates a Budget without creating a new revision in Hibernate Envers.
     * This is essential for reverting to a previous Budget state without polluting
     * the revision history.
     *
     * @param budget The Budget entity to be updated.
     * @return The updated Budget.
     */
    Budget updateWithoutAudit(Budget budget); // Newly added method

    /**
     * Deletes a Budget by its unique identifier.
     *
     * @param id The unique ID of the Budget to be deleted.
     * @return True if the deletion was successful, otherwise false.
     */
    boolean delete(Long id);

    /**
     * Retrieves all Budgets as an array.
     *
     * @return An array of all Budgets.
     */
    Budget[] findAll();
}
