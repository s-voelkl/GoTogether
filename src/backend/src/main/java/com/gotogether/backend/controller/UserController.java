package com.gotogether.backend.controller;

import com.gotogether.backend.dto.UserCreateDTO;
import com.gotogether.backend.model.User;
import com.gotogether.backend.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable UUID id) {
        try {
            return service.getUserById(id);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping()
    public List<User> getAllUsers() {
        try {
            return service.getAllUsers();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // public UUID createUser(UserCreateDTO dto) {
    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@RequestBody UserCreateDTO dto) {
        // catch exceptions and return appropriate HTTP status codes
        try {
            UUID id = service.createUser(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(id);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
