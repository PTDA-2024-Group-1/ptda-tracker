package com.ptda.tracker.repositories;

import com.ptda.tracker.models.admin.Admin;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AdminRepositoryTest {

    private final AdminRepository adminRepository;

    @Test
    void testInsertAdmin() {
        Admin admin = new Admin();
        admin.setEmail("admin@example.com");
        admin.setPassword("password");
        admin.setName("Admin User");
        Admin savedAdmin = adminRepository.save(admin);
        assertThat(savedAdmin.getId()).isNotNull();
    }

    @Test
    void testFindByEmail() {
        Admin admin = new Admin();
        admin.setName("Admin User");
        admin.setEmail("admin@example.com");
        admin.setPassword("password");

        adminRepository.save(admin);

        Optional<Admin> retrieved = adminRepository.findByEmail("admin@example.com");
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName()).isEqualTo("Admin User");
    }

    @Test
    void testUpdateAdmin() {
        Admin admin = new Admin();
        admin.setName("Admin User");
        admin.setEmail("admin@example.com");
        admin.setPassword("password");

        adminRepository.save(admin);

        admin.setName("Updated Admin");
        adminRepository.save(admin);

        Optional<Admin> retrieved = adminRepository.findByEmail("admin@example.com");
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName()).isEqualTo("Updated Admin");
    }

    @Test
    void testDeleteAdmin() {
        Admin admin = new Admin();
        admin.setName("Admin User");
        admin.setEmail("admin@example.com");
        admin.setPassword("password");

        adminRepository.save(admin);
        adminRepository.delete(admin);

        Optional<Admin> retrieved = adminRepository.findByEmail("admin@example.com");
        assertThat(retrieved).isNotPresent();
    }

    @Test
    void testFindById() {
        Admin admin = new Admin();
        admin.setName("Admin User");
        admin.setEmail("admin@example.com");
        admin.setPassword("password");

        adminRepository.save(admin);

        Optional<Admin> retrieved = adminRepository.findById(admin.getId());
        assertThat(retrieved).isPresent();
    }

    @Test
    void testFindAll(){
        Admin admin = new Admin();
        admin.setName("Admin User");
        admin.setEmail("admin@example.com");
        admin.setPassword("password");

        adminRepository.save(admin);

        Admin admin2 = new Admin();
        admin2.setName("Admin User 2");
        admin2.setEmail("admin2@example.com");

        adminRepository.save(admin2);

        assertThat(adminRepository.findAll()).hasSize(2);
    }
}