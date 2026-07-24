package com.gotogether.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gotogether.backend.model.Topic;

import java.util.UUID;

/**
 * Repository interface for Topic entity.
 * <p>
 * TopicRepository is a Spring Data JPA repository for the {@link Topic} entity.
 * It handles the retrieval and persistence of thematic categories (topics) that
 * challenges and users can be linked to.
 */
@Repository
public interface TopicRepository extends JpaRepository<Topic, UUID> {
    // findById(UUID) is inherited for free

    /**
     * Checks if a {@link Topic} with the specified name already exists in the
     * database.
     * <p>
     * Ensures that newly created topics or name updates do not result in duplicate
     * topic names.
     * 
     * @param name the topic name to check
     * @return true if a topic with the specified name exists, false otherwise
     */
    boolean existsByName(String name);
}
