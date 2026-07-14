package com.gotogether.backend.repository;

import com.gotogether.backend.model.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Challenge entity.
 * <p>
 * ChallengeRepository is a Spring Data JPA repository for the {@link Challenge}
 * entity.
 * It provides basic CRUD operations and custom query methods for managing
 * challenges
 * in the underlying database.
 * The repository extends JpaRepository, which provides methods like
 * findById, findAll, save, delete, etc.
 */
@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, UUID> {

    /**
     * Retrieves a {@link Challenge} by its verification code, ignoring case.
     * <p>
     * Searches the database for a challenge entity matching the provided
     * verification
     * code. The lookup is case-insensitive to ensure robust verification regardless
     * of user input styling.
     * 
     * @param verificationCode the code used to verify the challenge
     * @return an {@link Optional} containing the found challenge, or an empty
     *         Optional if none found
     */
    Optional<Challenge> findByVerificationCodeIgnoreCase(String verificationCode);
    // findById(UUID) is inherited for free
}
