package com.ptda.tracker.services;

import com.ptda.tracker.models.admin.Admin;
import com.ptda.tracker.repositories.AdminRepository;
import com.ptda.tracker.services.administration.AdminServiceHibernateImpl;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AdminServiceTest {

    private final AdminRepository adminRepository;
    private AdminServiceHibernateImpl adminService;

    @BeforeEach
    void setUp() {
        adminService = new AdminServiceHibernateImpl(adminRepository);
    }

    @Test
    void testGetById() {
        Admin admin = new Admin();
        admin.setName("Test Admin");
        admin.setEmail("admin@example.com");
        admin.setPassword("password");
        adminRepository.save(admin);

        Optional<Admin> foundAdmin = adminService.getById(admin.getId());

        assertThat(foundAdmin).isPresent();
        assertThat(foundAdmin.get().getId()).isEqualTo(admin.getId());
    }

    @Test
    void testGetAll() {
        Admin admin1 = new Admin();
        admin1.setName("Test Admin 1");
        admin1.setEmail("admin1@example.com");
        admin1.setPassword("password");
        adminRepository.save(admin1);

        Admin admin2 = new Admin();
        admin2.setName("Test Admin 2");
        admin2.setEmail("admin2@example.com");
        admin2.setPassword("password");
        adminRepository.save(admin2);

        List<Admin> admins = adminService.getAll();

        assertThat(admins).hasSize(2);
        assertThat(admins.get(0).getEmail()).isEqualTo("admin1@example.com");
        assertThat(admins.get(1).getEmail()).isEqualTo("admin2@example.com");
    }
}