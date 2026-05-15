package com.gotogether.backend.services;

import com.gotogether.backend.model.User;
import com.gotogether.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.gotogether.backend.dto.UserCreateDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

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

    public UUID createUser(UserCreateDTO dto) {
        // email must be unique
        if (repo.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists: " + dto.getEmail());
        }

        // email validation
        if (dto.getEmail() == null
                || dto.getEmail().isEmpty()
                || !dto.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RuntimeException("Invalid email address: " + dto.getEmail());
        }

        // username must not be empty
        if (dto.getUsername() == null || dto.getUsername().isEmpty()) {
            throw new RuntimeException("Username must not be empty: " + dto.getUsername());
        }

        // password must not be empty
        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new RuntimeException("Password must not be empty.");
        }

        // create user
        User user = repo.save(new User(
                dto.getUsername(),
                dto.getPassword(),
                dto.getEmail()));

        return user.getId();
    }
}