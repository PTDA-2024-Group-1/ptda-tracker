package com.ptda.tracker.repositories;

import com.ptda.tracker.models.tracker.ExpenseColumnMapping;
import com.ptda.tracker.models.tracker.ExpenseCategory;
import com.ptda.tracker.models.user.User;
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
public class ExpenseColumnMappingRepositoryTest {

    private final ExpenseColumnMappingRepository expenseColumnMappingRepository;

    @Test
    void testSaveAndFindById() {
        User user = User.builder().id(1L).build();
        ExpenseColumnMapping mapping = ExpenseColumnMapping.builder()
                .name("Test Column")
                .category(ExpenseCategory.OTHER)
                .createdBy(user)
                .build();
        expenseColumnMappingRepository.save(mapping);
        assertThat(mapping.getId()).isNotNull();

        Optional<ExpenseColumnMapping> foundMapping = expenseColumnMappingRepository.findById(mapping.getId());
        assertThat(foundMapping).isPresent();
        assertThat(foundMapping.get().getName()).isEqualTo("Test Column");
    }

    @Test
    void testUpdate() {
        User user = User.builder().id(1L).build(); // Supondo que o usuário já exista
        ExpenseColumnMapping mapping = ExpenseColumnMapping.builder()
                .name("Initial Column")
                .category(ExpenseCategory.OTHER)
                .createdBy(user)
                .build();
        expenseColumnMappingRepository.save(mapping);

        mapping.setName("Updated Column");
        expenseColumnMappingRepository.save(mapping);

        Optional<ExpenseColumnMapping> updatedMapping = expenseColumnMappingRepository.findById(mapping.getId());
        assertThat(updatedMapping).isPresent();
        assertThat(updatedMapping.get().getName()).isEqualTo("Updated Column");
    }

    @Test
    void testDelete() {
        User user = User.builder().id(1L).build(); // Supondo que o usuário já exista
        ExpenseColumnMapping mapping = ExpenseColumnMapping.builder()
                .name("Test Column")
                .category(ExpenseCategory.OTHER)
                .createdBy(user)
                .build();
        expenseColumnMappingRepository.save(mapping);
        Long id = mapping.getId();

        expenseColumnMappingRepository.deleteById(id);
        Optional<ExpenseColumnMapping> deletedMapping = expenseColumnMappingRepository.findById(id);
        assertThat(deletedMapping).isNotPresent();
    }
}