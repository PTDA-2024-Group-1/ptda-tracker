package com.ptda.tracker.services.administration;

import com.ptda.tracker.models.admin.Admin;
import com.ptda.tracker.repositories.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminServiceHibernateImpl implements AdminService {

    private final AdminRepository adminRepository;

    @Override
    public Optional<Admin> getById(Long id) {
        return adminRepository.findById(id);
    }

    @Override
    public Optional<Admin> getByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    @Override
    public Admin create(Admin admin) {
        return adminRepository.save(admin);
    }

    @Override
    public List<Admin> createAll(List<Admin> admins) {
        return adminRepository.saveAll(admins);
    }

    @Override
    public Admin update(Admin admin) {
        return adminRepository.save(admin);
    }

    @Override
    public boolean delete(Admin admin) {
        if (adminRepository.existsById(admin.getId())) {
            adminRepository.delete(admin);
            return true;
        }
        return false;
    }

    @Override
    public List<Admin> getAll() {
        return adminRepository.findAll();
    }
}
