package com.gotogether.backend.repository;

import com.gotogether.backend.model.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for Challenge entity.
 * ChallengeRepository is a Spring Data JPA repository for the Challenge entity.
 * It provides basic CRUD operations and custom query methods.
 * The repository is annotated with [at]Repository, which allows Spring to
 * detect it during component scanning and handle exceptions appropriately.
 * The ChallengeRepository extends JpaRepository, which provides methods like
 * findById, findAll, save, delete, etc.
 * Additionally, custom query methods can be defined by following Spring Data
 * JPA's method naming conventions.
 */
@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, UUID> {
    // findById(UUID) is inherited for free
}
