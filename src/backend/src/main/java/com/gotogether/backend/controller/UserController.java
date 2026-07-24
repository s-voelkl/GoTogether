package com.gotogether.backend.controller;

import com.gotogether.backend.dto.UserCreateDTO;
import com.gotogether.backend.dto.UserLoginDTO;
import com.gotogether.backend.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing users.
 * <p>
 * Provides endpoints for user signup, login, retrieval, and updating user
 * preferences such as social battery and interests.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    /**
     * Constructs a new UserController with the given UserService.
     *
     * @param service the service used for user operations
     */
    public UserController(UserService service) {
        this.service = service;
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the UUID of the user to retrieve
     * @return a ResponseEntity containing the UserDTO if found, or a 404 NOT FOUND
     *         status with an error message
     * @throws RuntimeException if the user is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(service.getUserById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Retrieves a list of all users.
     *
     * @return a ResponseEntity containing a list of UserDTOs, or a 500 INTERNAL
     *         SERVER ERROR status on failure
     * @throws RuntimeException if an error occurs during retrieval
     */
    @GetMapping()
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(service.getAllUsers());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Creates a new user account (signup).
     *
     * @param dto the data transfer object containing the user details
     * @return a ResponseEntity containing the UUID of the newly created user, or a
     *         400 BAD REQUEST status on validation failure
     * @throws RuntimeException if the validation fails or creation errors occur
     */
    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@RequestBody UserCreateDTO dto) {
        try {
            UUID id = service.createUser(dto.getUsername(), dto.getPassword(), dto.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(id);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Authenticates a user and logs them in.
     *
     * @param dto the data transfer object containing login credentials
     * @return a ResponseEntity containing the UUID of the logged-in user, or a 401
     *         UNAUTHORIZED status if authentication fails
     * @throws RuntimeException if the authentication fails
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginDTO dto) {
        try {
            UUID id = service.loginUser(dto.getEmail(), dto.getPassword());
            return ResponseEntity.ok(id);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /**
     * Updates a user's social battery preference.
     *
     * @param userId        the UUID of the user
     * @param socialBattery the new social battery value (0-100)
     * @return a ResponseEntity containing the updated social battery value, or a
     *         400 BAD REQUEST status on failure
     * @throws RuntimeException if the update fails
     */
    @PutMapping("/preferences/socialBattery/{userId}")
    public ResponseEntity<?> setUserPreferences(@PathVariable UUID userId, @RequestBody int socialBattery) {
        try {
            int updatedSocialBattery = service.setUserSocialBattery(userId, socialBattery);
            return ResponseEntity.ok(updatedSocialBattery);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Updates a user's interests (topics).
     *
     * @param userId      the UUID of the user
     * @param interestIds a list of topic UUIDs to set as the user's interests
     * @return a ResponseEntity containing the updated list of interest UUIDs, or a
     *         400 BAD REQUEST status on failure
     * @throws RuntimeException if the update fails
     */
    @PutMapping("/preferences/interests/{userId}")
    public ResponseEntity<?> setUserInterests(@PathVariable UUID userId, @RequestBody List<UUID> interestIds) {
        try {
            List<UUID> result = service.setUserInterests(userId, interestIds);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
