package com.ptda.tracker.services.admin;

import com.ptda.tracker.models.admin.Admin;
import com.ptda.tracker.models.assistance.Assistant;
import com.ptda.tracker.models.user.User;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleManagementServiceHibernateImpl implements RoleManagementService {
    private final EntityManager entityManager;

    @Override
    @Transactional
    public Assistant promoteUserToAssistant(User user) {
        if (user instanceof Assistant) {
            throw new IllegalArgumentException("User is already an Assistant or higher!");
        }

        // Update the user_type and assistant_level_id fields in the database
        String updateQuery = "UPDATE _user SET user_type = 'ASSISTANT', assistant_level_id = NULL WHERE id = :id";
        int updatedRows = entityManager.createNativeQuery(updateQuery)
                .setParameter("id", user.getId())
                .executeUpdate();

        if (updatedRows == 0) {
            throw new IllegalStateException("Failed to promote User with ID: " + user.getId());
        }

        // Reload the updated entity as Assistant
        return entityManager.find(Assistant.class, user.getId());
    }

    @Override
    @Transactional
    public Admin promoteUserToAdmin(User user) {
        if (user instanceof Admin) {
            throw new IllegalArgumentException("User is already an Admin or higher!");
        }

        // Update the user_type and assistant_level_id fields in the database
        String updateQuery = "UPDATE _user SET user_type = 'ADMIN', assistant_level_id = NULL WHERE id = :id";
        int updatedRows = entityManager.createNativeQuery(updateQuery)
                .setParameter("id", user.getId())
                .executeUpdate();

        if (updatedRows == 0) {
            throw new IllegalStateException("Failed to promote User with ID: " + user.getId());
        }

        // Reload the updated entity as Admin
        return entityManager.find(Admin.class, user.getId());
    }

    @Override
    @Transactional
    public User demoteAssistant(Assistant assistant) {
        if (assistant == null) {
            throw new IllegalArgumentException("User is not an Assistant!");
        }

        // Update user_type back to 'USER'
        String updateQuery = "UPDATE _user SET user_type = 'USER' WHERE id = :id";
        int updatedRows = entityManager.createNativeQuery(updateQuery)
                .setParameter("id", assistant.getId())
                .executeUpdate();

        if (updatedRows == 0) {
            throw new IllegalStateException("Failed to demote Assistant with ID: " + assistant.getId());
        }

        // Reload the entity as a User
        return entityManager.find(User.class, assistant.getId());
    }

    @Override
    @Transactional
    public Admin promoteAssistant(User user) {
        if (!(user instanceof Assistant)) {
            throw new IllegalArgumentException("User is not an Assistant!");
        }

        // Update the user_type and assistant_level_id fields in the database
        String updateQuery = "UPDATE _user SET user_type = 'ADMIN' WHERE id = :id";
        int updatedRows = entityManager.createNativeQuery(updateQuery)
                .setParameter("id", user.getId())
                .executeUpdate();

        if (updatedRows == 0) {
            throw new IllegalStateException("Failed to promote Assistant with ID: " + user.getId());
        }

        // Reload the updated entity as Admin
        return entityManager.find(Admin.class, user.getId());
    }

    @Override
    @Transactional
    public User demoteAdminToUser(Admin admin) {
        if (admin == null) {
            throw new IllegalArgumentException("User is not an Admin!");
        }

        // Update user_type back to 'USER'
        String updateQuery = "UPDATE _user SET user_type = 'USER' WHERE id = :id";

        int updatedRows = entityManager.createNativeQuery(updateQuery)
                .setParameter("id", admin.getId())
                .executeUpdate();

        if (updatedRows == 0) {
            throw new IllegalStateException("Failed to demote Admin with ID: " + admin.getId());
        }

        // Reload the entity as a User
        return entityManager.find(User.class, admin.getId());
    }

    @Override
    @Transactional
    public Assistant demoteAdminToAssistant(Admin admin) {
        if (admin == null) {
            throw new IllegalArgumentException("User is not an Admin!");
        }

        // Update the user_type back to 'ASSISTANT'
        String updateQuery = "UPDATE _user SET user_type = 'ASSISTANT' WHERE id = :id";

        int updatedRows = entityManager.createNativeQuery(updateQuery)
                .setParameter("id", admin.getId())
                .executeUpdate();

        if (updatedRows == 0) {
            throw new IllegalStateException("Failed to demote Admin with ID: " + admin.getId());
        }

        // Reload the entity as an Assistant
        return entityManager.find(Assistant.class, admin.getId());
    }

}
