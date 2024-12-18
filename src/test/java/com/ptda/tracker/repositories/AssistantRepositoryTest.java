package com.ptda.tracker.repositories;

import com.ptda.tracker.models.assistance.Assistant;
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
public class AssistantRepositoryTest {

    private final AssistantRepository assistantRepository;

    @Test
    void testInsertAssistant() {
        Assistant assistant = new Assistant();
        assistant.setEmail("assistant@example.com");
        assistant.setPassword("password");
        assistant.setName("Assistant User");

        Assistant savedAssistant = assistantRepository.save(assistant);
        assertThat(savedAssistant.getId()).isNotNull();
    }

    @Test
    void testFindByEmail() {
        Assistant assistant = new Assistant();
        assistant.setName("Assistant User");
        assistant.setEmail("assistant@example.com");
        assistant.setPassword("password");

        assistantRepository.save(assistant);

        Optional<Assistant> retrieved = assistantRepository.findByEmail("assistant@example.com");
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName()).isEqualTo("Assistant User");
    }

    @Test
    void testUpdateAssistant() {
        Assistant assistant = new Assistant();
        assistant.setName("Assistant User");
        assistant.setEmail("assistant@example.com");
        assistant.setPassword("password");

        assistantRepository.save(assistant);

        assistant.setName("Updated Assistant");
        assistantRepository.save(assistant);

        Optional<Assistant> retrieved = assistantRepository.findByEmail("assistant@example.com");
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName()).isEqualTo("Updated Assistant");
    }

    @Test
    void testDeleteAssistant() {
        Assistant assistant = new Assistant();
        assistant.setName("Assistant User");
        assistant.setEmail("assistant@example.com");
        assistant.setPassword("password");

        assistantRepository.save(assistant);
        assistantRepository.delete(assistant);

        Optional<Assistant> retrieved = assistantRepository.findByEmail("assistant@example.com");
        assertThat(retrieved).isNotPresent();
    }
}
