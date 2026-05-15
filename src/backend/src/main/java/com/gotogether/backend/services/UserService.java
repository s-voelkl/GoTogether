package com.gotogether.backend.services;

import com.gotogether.backend.model.User;
import com.gotogether.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.gotogether.backend.dto.UserCreateDTO;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public User getUserById(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getAllUsers() {
        return repo.findAll();
    }

    public UUID createUser(String name, String passwordHash, String email) {
        // email must be unique
        if (repo.existsByEmail(email)) {
            throw new RuntimeException("Email already exists: " + email);
        }

        // email validation
        if (email == null
                || email.isEmpty()
                || !email.matches(EMAIL_REGEX)) {
            throw new RuntimeException("Invalid email address: " + email);
        }

        // username must not be empty
        if (name == null || name.isEmpty()) {
            throw new RuntimeException("Username must not be empty: " + name);
        }

        // password must not be empty
        if (passwordHash == null || passwordHash.isEmpty()) {
            throw new RuntimeException("Password must not be empty.");
        }

        // create user
        User user = repo.save(new User(
                name,
                passwordHash,
                email));

        return user.getId();
    }

    public UUID loginUser(String email, String passwordHash) {
        // validate email input
        if (email == null
                || email.isEmpty()
                || !email.matches(EMAIL_REGEX)) {
            throw new RuntimeException("Invalid email address: " + email);
        }

        // validate password input
        if (passwordHash == null || passwordHash.isEmpty()) {
            throw new RuntimeException("Password must not be empty.");
        }

        // find user by email
        User user = repo.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("No user found with email: " + email);
        }

        // check password
        if (!user.getPasswordHash().equals(passwordHash)) {
            throw new RuntimeException("Invalid password.");
        }

        // update last login time
        user.setLastLogin(java.time.LocalDateTime.now());
        repo.save(user);
        return user.getId();
    }

    public void setUserSocialBattery(UUID userId, int socialBattery) {
        if (socialBattery < 0 || socialBattery > 100) {
            throw new RuntimeException("Social battery must be between 0 and 100: " + socialBattery);
        }

        if (userId == null) {
            throw new RuntimeException("User ID must not be null.");
        }

        User user = repo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        user.setSocialBattery(socialBattery);
        repo.save(user);
    }

    // TODO: implement real interests logic
    public void setUserInterests(UUID userId, List<String> interests) {
        if (userId == null) {
            throw new RuntimeException("User ID must not be null.");
        }

        User user = repo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // user.setInterests(interests);
        repo.save(user);
    }

}