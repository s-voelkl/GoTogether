package com.gotogether.backend.repository;

import com.gotogether.backend.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for Company entity.
 * CompanyRepository is a Spring Data JPA repository for the Company entity.
 * It provides basic CRUD operations and custom query methods.
 * The repository is annotated with [at]Repository, which allows Spring to
 * detect it during component scanning and handle exceptions appropriately.
 * The CompanyRepository extends JpaRepository, which provides methods like
 * findById, findAll, save, delete, etc.
 * Additionally, custom query methods can be defined by following Spring Data
 * JPA's method naming conventions, such as existsByEmail to check for the
 * existence of a company by email.
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
    // findById(UUID) is inherited for free

    boolean existsByEmail(String email);

    Company findByEmail(String email);

}
