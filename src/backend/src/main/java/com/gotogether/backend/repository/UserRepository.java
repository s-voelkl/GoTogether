package com.gotogether.backend.repository;

import com.gotogether.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for User entity.
 * UserRepository is a Spring Data JPA repository for the User entity.
 * It provides basic CRUD operations and custom query methods.
 * The repository is annotated with [at]Repository, which allows Spring to
 * detect it during component scanning and handle exceptions appropriately.
 * The UserRepository extends JpaRepository, which provides methods like
 * findById, findAll, save, delete, etc.
 * Additionally, custom query methods can be defined by following Spring Data
 * JPA's method naming conventions, such as existsByEmail to check for the
 * existence of a user by email.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    // findById(UUID) is inherited for free

    boolean existsByEmail(String email);

    User findByEmail(String email);

    // TODO: set real interests method
    // void setInterests(List<String> interests);

}
