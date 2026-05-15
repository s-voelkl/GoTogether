package com.gotogether.backend.services;

import com.gotogether.backend.dto.UserCreateDTO;
import com.gotogether.backend.model.User;
import com.gotogether.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserById_UserExists_ReturnsUser() {
        // Arrange
        UUID id = UUID.randomUUID();
        User mockUser = new User(id, "Test User", "hash", "test@test.com", 100, 0, 0, LocalDateTime.now());
        when(userRepository.findById(id)).thenReturn(Optional.of(mockUser));

        // Act
        User result = userService.getUserById(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Test User", result.getName());
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void getUserById_UserDoesNotExist_ThrowsRuntimeException() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> userService.getUserById(id));
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void getAllUsers_UsersExist_ReturnsList() {
        // Arrange
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        User user1 = new User(id1, "User One", "hash1", "one@test.com", 50, 0, 0, LocalDateTime.now());
        User user2 = new User(id2, "User Two", "hash2", "two@test.com", 80, 0, 0, LocalDateTime.now());
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void createUser_EmailAlreadyExists_ThrowsRuntimeException() {
        // Arrange
        UserCreateDTO dto = new UserCreateDTO("testuser", "password", "test@test.com");
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> userService.createUser(dto));
        assertTrue(exception.getMessage().contains("Email already exists: test@test.com"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_EmptyEmail_ThrowsRuntimeException() {
        // Arrange
        UserCreateDTO dto = new UserCreateDTO("testuser", "password", "");

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> userService.createUser(dto));
        assertTrue(exception.getMessage().contains("Invalid email address: "));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_InvalidEmailFormat_ThrowsRuntimeException() {
        // Arrange
        UserCreateDTO dto = new UserCreateDTO("testuser", "password", "not_an_email");

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> userService.createUser(dto));
        assertTrue(exception.getMessage().contains("Invalid email address: not_an_email"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_EmptyUsername_ThrowsRuntimeException() {
        // Arrange
        UserCreateDTO dto = new UserCreateDTO("", "password", "test@test.com");

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> userService.createUser(dto));
        assertTrue(exception.getMessage().contains("Username must not be empty"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_EmptyPassword_ThrowsRuntimeException() {
        // Arrange
        UserCreateDTO dto = new UserCreateDTO("testuser", "", "test@test.com");

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> userService.createUser(dto));
        assertTrue(exception.getMessage().contains("Password must not be empty"));
        verify(userRepository, never()).save(any(User.class));
    }
}