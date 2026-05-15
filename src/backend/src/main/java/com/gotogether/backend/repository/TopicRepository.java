package com.gotogether.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gotogether.backend.model.Topic;

import java.util.UUID;

/**
 * Repository interface for Topic entity.
 */
@Repository
public interface TopicRepository extends JpaRepository<Topic, UUID> {
    // findById(UUID) is inherited for free

    boolean existsByName(String name);
}
