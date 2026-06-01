package com.gotogether.backend.services;

import com.gotogether.backend.dto.UserDTO;
import com.gotogether.backend.mapper.UserMapper;
import com.gotogether.backend.model.User;
import com.gotogether.backend.repository.UserRepository;
import com.gotogether.backend.repository.CompanyRepository;
import com.gotogether.backend.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;
    private final TopicRepository topicRepo;
    private final CompanyRepository companyRepo;

    private final UserMapper userMapper;

    public UserDTO getUserById(UUID id) {
        return repo.findById(id).map(userMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<UserDTO> getAllUsers() {
        return repo.findAll().stream().map(userMapper::toDTO).toList();
    }

    public UUID createUser(String name, String password, String email) {
        // email must be unique in both users and companies
        if (repo.existsByEmail(email.trim().toLowerCase())) {
            throw new RuntimeException("Email already exists: " + email);
        }

        if (companyRepo.existsByEmail(email.trim().toLowerCase())) {
            throw new RuntimeException("Email already exists at companies: " + email);
        }

        // email validation
        if (email == null
                || email.trim().isEmpty()
                || !EmailValidator.getInstance().isValid(email.trim().toLowerCase())) {
            throw new RuntimeException("Invalid email address: " + email);
        }

        // username must not be empty
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Username must not be empty: " + name);
        }

        // password must not be empty
        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("Password must not be empty.");
        }

        // create user
        User user = repo.save(new User(
                name.trim(),
                password,
                email.trim().toLowerCase()));

        return user.getId();
    }

    public UUID loginUser(String email, String password) {
        // validate email input
        if (email == null
                || email.trim().isEmpty()
                || !EmailValidator.getInstance().isValid(email.trim().toLowerCase())) {
            throw new RuntimeException("Invalid email address: " + email);
        }

        // validate password input
        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("Password must not be empty.");
        }

        // find user by email
        User user = repo.findByEmail(email.toLowerCase());
        if (user == null) {
            throw new RuntimeException("No user found with email: " + email);
        }

        // check password
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password.");
        }

        // update last login time
        user.setLastLogin(java.time.LocalDateTime.now());
        repo.save(user);
        return user.getId();
    }

    public int setUserSocialBattery(UUID userId, int socialBattery) {
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
        return socialBattery;
    }

    public List<UUID> setUserInterests(UUID userId, List<UUID> interestIds) {
        if (userId == null) {
            throw new RuntimeException("User ID must not be null.");
        }

        if (interestIds == null) {
            throw new RuntimeException("Interest IDs must not be null.");
        }

        // filter out duplicates (use Collectors.toList for broader Java compatibility)
        interestIds = interestIds.stream().distinct().collect(Collectors.toList());

        // validate topic existence
        for (UUID interestId : interestIds) {
            if (!topicRepo.existsById(interestId)) {
                throw new RuntimeException("Interest not found: " + interestId);
            }
        }

        User user = repo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        user.setInterests(interestIds);
        repo.save(user);
        return interestIds;
    }

}