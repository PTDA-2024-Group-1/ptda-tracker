package com.ptda.tracker.services.user;

import com.ptda.tracker.models.user.TierLimit;
import com.ptda.tracker.repositories.TierLimitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TierLimitServiceHibernateImpl implements TierLimitService {

    private final TierLimitRepository tierLimitRepository;

    @Override
    public List<TierLimit> getAll() {
        return tierLimitRepository.findAll();
    }

    @Override
    public List<TierLimit> getByTierId(Long tierId) {
        return tierLimitRepository.findByTierId(tierId);
    }

    @Override
    public TierLimit create(TierLimit tierLimits) {
        return tierLimitRepository.save(tierLimits);
    }

    @Override
    public List<TierLimit> create(List<TierLimit> tierLimits) {
        return tierLimitRepository.saveAll(tierLimits);
    }

    @Override
    public TierLimit update(TierLimit tierLimits) {
        return tierLimitRepository.save(tierLimits);
    }

    @Override
    public boolean deleteById(Long id) {
        tierLimitRepository.deleteById(id);
        return !tierLimitRepository.existsById(id);
    }

    @Override
    public boolean deleteAllByTierId(Long tierId) {
        tierLimitRepository.deleteAllByTierId(tierId);
        return tierLimitRepository.findByTierId(tierId).isEmpty();
    }
}
