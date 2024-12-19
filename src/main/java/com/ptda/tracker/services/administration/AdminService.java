package com.ptda.tracker.services.administration;

import com.ptda.tracker.models.admin.Admin;

import java.util.List;
import java.util.Optional;

public interface AdminService {

    Optional<Admin> getById(Long id);

    Optional<Admin> getByEmail(String email);

    Admin create(Admin admin);

    Admin update(Admin admin);

    boolean delete(Admin admin);

    List<Admin> getAll();
}
