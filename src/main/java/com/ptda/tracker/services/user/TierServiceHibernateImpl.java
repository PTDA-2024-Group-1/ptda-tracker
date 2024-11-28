package com.ptda.tracker.services.user;

import com.ptda.tracker.models.user.Tier;
import com.ptda.tracker.repositories.TierRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TierServiceHibernateImpl implements TierService {
    private final TierRepository tierRepository;

    @Override
    public List<Tier> getAll() {
        return tierRepository.findAll();
    }

    @Override
    public Optional<Tier> getById(Long id) {
        return tierRepository.findById(id);
    }

    @Override
    public Tier getByName(String name) {
        return tierRepository.findByName(name);
    }

    @Override
    public List<Tier> getTiersByPoints(int points) {
        return tierRepository.findByPointsLessThanEqual(points);
    }

    @Override
    public Optional<Tier> getTopTierByPoints(int points) {
        return tierRepository.findTopByPointsLessThanEqualOrderByPointsDesc(points);
    }

    @Override
    @Transactional
    public Tier create(Tier tier) {
        return tierRepository.save(tier);
    }

    @Override
    @Transactional
    public List<Tier> create(List<Tier> tiers) {
        return tierRepository.saveAll(tiers);
    }

    @Override
    @Transactional
    public Tier update(Tier tier) {
        return tierRepository.save(tier);
    }

    @Override
    @Transactional
    public boolean delete(Tier tier) {
        try {
            tierRepository.delete(tier);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
