package com.gotogether.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service responsible for handling security-related operations,
 * specifically password hashing and verification.
 */
@Service
@RequiredArgsConstructor
public class SecurityService {

    private final PasswordEncoder passwordEncoder;

    /**
     * Hashes the given raw password using the configured PasswordEncoder.
     *
     * @param rawPassword the raw, unencrypted password to hash
     * @return the hashed password string
     * @throws RuntimeException if the raw password is null or empty
     */
    public String hashPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new RuntimeException("Password must not be empty.");
        }

        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Verifies if the given raw password matches the provided hashed password.
     *
     * @param rawPassword  the raw, unencrypted password to check
     * @param passwordHash the hashed password to match against
     * @return true if the passwords match, false otherwise
     * @throws RuntimeException if the raw password is null or empty
     */
    public boolean passwordMatches(String rawPassword, String passwordHash) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new RuntimeException("Password must not be empty.");
        }

        if (passwordHash == null || passwordHash.trim().isEmpty()) {
            return false;
        }

        return passwordEncoder.matches(rawPassword, passwordHash);
    }
}