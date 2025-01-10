package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.BudgetAccess;
import com.ptda.tracker.models.tracker.BudgetAccessLevel;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.repositories.BudgetAccessRepository;
import com.ptda.tracker.services.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BudgetAccessServiceHibernateImpl implements BudgetAccessService {

    private final BudgetAccessRepository budgetAccessRepository;
    private final UserService userService;

    @Override
    public List<BudgetAccess> getAllByUserId(Long userId) {
        return budgetAccessRepository.findAllByUserId(userId);
    }

    @Override
    public List<BudgetAccess> getRecentByUserId(Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return budgetAccessRepository.findAllByUserIdOrderByBudgetUpdatedAtDesc(userId, pageable);
    }

    @Override
    public int getCountByUserId(Long userId) {
        return budgetAccessRepository.countByUserId(userId);
    }

    @Override
    public List<BudgetAccess> getAllByBudgetId(Long budgetId) {
        return budgetAccessRepository.findAllByBudgetId(budgetId);
    }

    @Override
    public boolean hasAccess(Long budgetId, Long userId, BudgetAccessLevel requiredAccessLevel) {
        Optional<BudgetAccess> budgetAccess = budgetAccessRepository.findByBudgetIdAndUserId(budgetId, userId);
        return budgetAccess.isPresent() && budgetAccess.get().getAccessLevel().compareTo(requiredAccessLevel) <= 0;
    }

    @Override
    public boolean hasAccess(Long budgetId, String userEmail, BudgetAccessLevel requiredAccessLevel) {
        Optional<User> user = userService.getByEmail(userEmail);
        return user.isPresent() && hasAccess(budgetId, user.get().getId(), requiredAccessLevel);
    }

    @Override
    public boolean getAccessByBudgetIdAndUserId(Long budgetId, Long userId) {
        return budgetAccessRepository.existsByBudgetIdAndUserId(budgetId, userId);
    }

    @Override
    @Transactional
    public BudgetAccess create(Long budgetId, Long userId, BudgetAccessLevel accessLevel) {
        if (budgetId == null || userId == null || accessLevel == null) {
            throw new IllegalArgumentException("Budget ID, User ID, and Access Level must not be null.");
        }
        return budgetAccessRepository.save(
                BudgetAccess.builder()
                        .budget(Budget.builder().id(budgetId).build())
                        .user(User.builder().id(userId).build())
                        .accessLevel(accessLevel)
                        .build()
        );
    }

    @Override
    @Transactional
    public BudgetAccess create(Long budgetId, String userEmail, BudgetAccessLevel accessLevel) {
        if (budgetId == null || userEmail == null || accessLevel == null) {
            throw new IllegalArgumentException("Budget ID, User ID, and Access Level must not be null.");
        }
        Optional<User> user = userService.getByEmail(userEmail);
        if (user.isPresent()) {
            return budgetAccessRepository.save(
                    BudgetAccess.builder()
                            .budget(Budget.builder().id(budgetId).build())
                            .user(user.get())
                            .accessLevel(accessLevel)
                            .build()
            );
        }
        throw new IllegalArgumentException("User with email " + userEmail + " does not exist.");
    }

    @Override
    public List<BudgetAccess> createAll(List<BudgetAccess> accesses) {
        return budgetAccessRepository.saveAll(accesses);
    }

    @Override
    @Transactional
    public BudgetAccess update(BudgetAccess access) {
        return budgetAccessRepository.save(access);
    }

    @Override
    @Transactional
    public boolean delete(Long accessId) {
        if (budgetAccessRepository.existsById(accessId)) {
            budgetAccessRepository.deleteById(accessId);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean deleteAllByUserId(Long userId) {
        return !budgetAccessRepository.deleteAllByUserId(userId).isEmpty();
    }
}
