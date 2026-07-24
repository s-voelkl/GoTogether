package com.gotogether.backend.repository;

import com.gotogether.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for User entity.
 * <p>
 * UserRepository is a Spring Data JPA repository for the {@link User} entity.
 * It provides basic CRUD operations for user accounts, as well as methods
 * for email-based lookups.
 * The repository extends JpaRepository, which provides methods like
 * findById, findAll, save, delete, etc.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    // findById(UUID) is inherited for free

    /**
     * Checks whether a {@link User} with the given email address already exists.
     * <p>
     * Used primarily during registration or account updates to guarantee email
     * uniqueness amongst users.
     * 
     * @param email the email address to check for existence
     * @return true if a user with the specified email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Retrieves a {@link User} by their email address.
     * <p>
     * Fetches the user entity associated with the given email, which is critical
     * for authenticating the user during login.
     * 
     * @param email the email address of the user to find
     * @return the {@link User} entity matching the specified email, or null if not
     *         found
     */
    User findByEmail(String email);

}
