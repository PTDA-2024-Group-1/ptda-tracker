package com.ptda.tracker.services;

import com.ptda.tracker.models.assistance.Assistant;
import com.ptda.tracker.repositories.AssistantRepository;
import com.ptda.tracker.services.assistance.AssistantServiceHibernateImpl;
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
public class AssistantServiceTest {

    private final AssistantRepository assistantRepository;
    private AssistantServiceHibernateImpl assistantService;

    @BeforeEach
    void setUp() {
        assistantService = new AssistantServiceHibernateImpl(assistantRepository);
    }

    @Test
    void testGetById() {
        Assistant assistant = new Assistant();
        assistant.setName("Test Assistant");
        assistant.setEmail("assistant@example.com");
        assistant.setPassword("password");
        assistantRepository.save(assistant);

        Optional<Assistant> foundAssistant = assistantService.getById(assistant.getId());

        assertThat(foundAssistant).isPresent();
        assertThat(foundAssistant.get().getId()).isEqualTo(assistant.getId());
    }

    @Test
    void testGetAll() {
        Assistant assistant1 = new Assistant();
        assistant1.setName("Test Assistant 1");
        assistant1.setEmail("assistant1@example.com");
        assistant1.setPassword("password");
        assistantRepository.save(assistant1);

        Assistant assistant2 = new Assistant();
        assistant2.setName("Test Assistant 2");
        assistant2.setEmail("assistant2@example.com");
        assistant2.setPassword("password");
        assistantRepository.save(assistant2);

        List<Assistant> assistants = assistantService.getAll();

        assertThat(assistants).hasSize(2);
        assertThat(assistants.get(0).getEmail()).isEqualTo("assistant1@example.com");
        assertThat(assistants.get(1).getEmail()).isEqualTo("assistant2@example.com");
    }
}