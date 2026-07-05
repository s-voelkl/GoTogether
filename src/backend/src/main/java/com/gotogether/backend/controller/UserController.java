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

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(service.getUserById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(service.getAllUsers());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@RequestBody UserCreateDTO dto) {
        try {
            UUID id = service.createUser(dto.getUsername(), dto.getPassword(), dto.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(id);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginDTO dto) {
        try {
            UUID id = service.loginUser(dto.getEmail(), dto.getPassword());
            return ResponseEntity.ok(id);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PutMapping("/preferences/socialBattery/{userId}")
    public ResponseEntity<?> setUserPreferences(@PathVariable UUID userId, @RequestBody int socialBattery) {
        try {
            int updatedSocialBattery = service.setUserSocialBattery(userId, socialBattery);
            return ResponseEntity.ok(updatedSocialBattery);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

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
