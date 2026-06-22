package com.gotogether.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final PasswordEncoder passwordEncoder;

    public String hashPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new RuntimeException("Password must not be empty.");
        }

        return passwordEncoder.encode(rawPassword);
    }

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