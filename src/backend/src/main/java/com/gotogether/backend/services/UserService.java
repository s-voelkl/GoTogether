package com.gotogether.backend.services;

import com.gotogether.backend.dto.UserDTO;
import com.gotogether.backend.mapper.UserMapper;
import com.gotogether.backend.model.Topic;
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

/**
 * Service handling user-related operations, including retrieving users,
 * creating accounts,
 * authentication, and managing user-specific attributes like social battery and
 * interests.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;
    private final TopicRepository topicRepo;
    private final CompanyRepository companyRepo;

    private final UserMapper userMapper;
    private final SecurityService securityService;

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the UUID of the user to find
     * @return the user data transfer object
     * @throws RuntimeException if the user is not found
     */
    public UserDTO getUserById(UUID id) {
        if (id == null) {
            throw new RuntimeException("User ID must not be null.");
        }

        return repo.findById(id).map(userMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Retrieves all registered users in the system.
     *
     * @return a list of user data transfer objects
     */
    public List<UserDTO> getAllUsers() {
        return repo.findAll().stream().map(userMapper::toDTO).toList();
    }

    /**
     * Creates a new user account if validation passes and the email is unique
     * across users and companies.
     *
     * @param name     the username
     * @param password the unhashed password
     * @param email    a valid, unique email address
     * @return the UUID of the newly created user
     * @throws RuntimeException if inputs are invalid or the email is already in use
     */
    public UUID createUser(String name, String password, String email) {
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

        String normalizedEmail = email.trim().toLowerCase();

        // email must be unique in both users and companies
        if (repo.existsByEmail(normalizedEmail)) {
            throw new RuntimeException("Email already exists: " + email);
        }

        if (companyRepo.existsByEmail(normalizedEmail)) {
            throw new RuntimeException("Email already exists at companies: " + email);
        }

        String passwordHash = securityService.hashPassword(password);

        // create user
        User user = repo.save(new User(
                name.trim(),
                passwordHash,
                normalizedEmail));

        return user.getId();
    }

    /**
     * Authenticates a user and updates their last login timestamp upon success.
     *
     * @param email    the email of the user to log in
     * @param password the associated password
     * @return the UUID of the logged-in user
     * @throws RuntimeException if authentication fails
     */
    public UUID loginUser(String email, String password) {
        User user = authenticateUser(email, password);

        user.setLastLogin(java.time.LocalDateTime.now());
        repo.save(user);

        return user.getId();
    }

    /**
     * Authenticates a user by checking email validity, looking up the user, and
     * explicitly
     * comparing the password using the associated password encoder.
     *
     * @param email    the email of the user
     * @param password the plain text password to check
     * @return the authenticated user entity
     * @throws RuntimeException if the email is incorrectly formatted, the user
     *                          doesn't exist, or the password does not match
     */
    public User authenticateUser(String email, String password) {
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

        String normalizedEmail = email.trim().toLowerCase();

        // find user by email
        User user = repo.findByEmail(normalizedEmail);
        if (user == null) {
            throw new RuntimeException("No user found with email: " + email);
        }

        // check password
        if (!securityService.passwordMatches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password.");
        }

        return user;
    }

    /**
     * Updates the user's current social battery.
     *
     * @param userId        the UUID of the user
     * @param socialBattery the new social battery level, must be between 0 and 100
     * @return the newly updated social battery level
     * @throws RuntimeException if the user doesn't exist or the value is out of
     *                          bounds
     */
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

    /**
     * Updates the user's selected interests (topics). Deduplicates inputs and
     * resolves
     * UUIDs to the actual {@link Topic} entities.
     *
     * @param userId      the UUID of the user to update
     * @param interestIds a list of topic UUIDs the user is interested in
     * @return a list of UUIDs corresponding to the set interests
     * @throws RuntimeException if the user or any specific interest ID is not found
     */
    public List<UUID> setUserInterests(UUID userId, List<UUID> interestIds) {
        if (userId == null) {
            throw new RuntimeException("User ID must not be null.");
        }

        if (interestIds == null) {
            throw new RuntimeException("Interest IDs must not be null.");
        }

        // filter out duplicates (use Collectors.toList for broader Java compatibility)
        interestIds = interestIds.stream().distinct().collect(Collectors.toList());

        // validate topic existence and resolve to entities
        List<Topic> topics = new java.util.ArrayList<>(interestIds.size());
        for (UUID interestId : interestIds) {
            if (interestId == null) {
                continue; // skip null IDs
            }

            Topic topic = topicRepo.findById(interestId)
                    .orElseThrow(() -> new RuntimeException("Interest not found: " + interestId));
            topics.add(topic);
        }

        User user = repo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        user.setInterests(topics);
        repo.save(user);
        return interestIds;
    }

}