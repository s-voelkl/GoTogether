package com.gotogether.backend.repository;

import com.gotogether.backend.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for Company entity.
 * <p>
 * CompanyRepository is a Spring Data JPA repository for the {@link Company}
 * entity.
 * It provides basic CRUD operations and custom query methods for accessing and
 * managing company data.
 * The repository extends JpaRepository, which provides methods like
 * findById, findAll, save, delete, etc.
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
    // findById(UUID) is inherited for free

    /**
     * Checks whether a {@link Company} with the given email address already exists.
     * <p>
     * Used primarily during registration or updates to ensure email uniqueness
     * across different companies in the system.
     * 
     * @param email the email address to check for existence
     * @return true if a company with the specified email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Retrieves a {@link Company} based on its email address.
     * <p>
     * Looks up the company credentials and details associated with the given email.
     * 
     * @param email the email address of the company to find
     * @return the {@link Company} matching the specified email, or null if not
     *         found
     */
    Company findByEmail(String email);

}
