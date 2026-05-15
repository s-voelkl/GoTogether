package com.gotogether.backend.services;

import com.gotogether.backend.dto.UserCreateDTO;
import com.gotogether.backend.model.User;
import com.gotogether.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.createUser(dto.getUsername(), dto.getPasswordHash(), dto.getEmail()));
        assertTrue(exception.getMessage().contains("Email already exists: test@test.com"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_EmptyEmail_ThrowsRuntimeException() {
        // Arrange
        UserCreateDTO dto = new UserCreateDTO("testuser", "password", "");

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.createUser(dto.getUsername(), dto.getPasswordHash(), dto.getEmail()));
        assertTrue(exception.getMessage().contains("Invalid email address: "));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_InvalidEmailFormat_ThrowsRuntimeException() {
        // Arrange
        UserCreateDTO dto = new UserCreateDTO("testuser", "password", "not_an_email");

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.createUser(dto.getUsername(), dto.getPasswordHash(), dto.getEmail()));
        assertTrue(exception.getMessage().contains("Invalid email address: not_an_email"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_EmptyUsername_ThrowsRuntimeException() {
        // Arrange
        UserCreateDTO dto = new UserCreateDTO("", "password", "test@test.com");

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.createUser(dto.getUsername(), dto.getPasswordHash(), dto.getEmail()));
        assertTrue(exception.getMessage().contains("Username must not be empty"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_EmptyPassword_ThrowsRuntimeException() {
        // Arrange
        UserCreateDTO dto = new UserCreateDTO("testuser", "", "test@test.com");

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.createUser(dto.getUsername(), dto.getPasswordHash(), dto.getEmail()));
        assertTrue(exception.getMessage().contains("Password must not be empty"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginUser_ValidCredentials_ReturnsUserId() {
        // Arrange
        UUID expectedId = UUID.randomUUID();
        User mockUser = new User(expectedId, "Test User", "correct_hash", "test@test.com", 100, 0, 0,
                LocalDateTime.now());
        when(userRepository.findByEmail("test@test.com")).thenReturn(mockUser);

        // Act
        UUID resultId = userService.loginUser("test@test.com", "correct_hash");

        // Assert
        assertEquals(expectedId, resultId);
        verify(userRepository, times(1)).findByEmail("test@test.com");
    }

    @Test
    void loginUser_InvalidEmail_ThrowsRuntimeException() {
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> userService.loginUser("not_an_email", "hash"));
        assertTrue(exception.getMessage().contains("Invalid email address: not_an_email"));
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void loginUser_EmptyPassword_ThrowsRuntimeException() {
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> userService.loginUser("test@test.com", ""));
        assertEquals("Password must not be empty.", exception.getMessage());
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void loginUser_UserNotFound_ThrowsRuntimeException() {
        // Arrange
        when(userRepository.findByEmail("test@test.com")).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.loginUser("test@test.com", "hash"));
        assertEquals("No user found with email: test@test.com", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("test@test.com");
    }

    @Test
    void loginUser_InvalidPassword_ThrowsRuntimeException() {
        // Arrange
        UUID id = UUID.randomUUID();
        User mockUser = new User(id, "Test User", "correct_hash", "test@test.com", 100, 0, 0, LocalDateTime.now());
        when(userRepository.findByEmail("test@test.com")).thenReturn(mockUser);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.loginUser("test@test.com", "wrong_hash"));
        assertEquals("Invalid password.", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("test@test.com");
    }

    // Test update of last login date
    @Test
    void loginUser_ValidCredentials_UpdatesLastLogin() {
        // Arrange: set user with old last login time
        UUID id = UUID.randomUUID();
        LocalDateTime oldLoginTime = LocalDateTime.now().minusDays(1);
        User mockUser = new User(id, "Test User", "correct_hash", "test@test.com", 100, 0, 0, oldLoginTime);
        when(userRepository.findByEmail("test@test.com")).thenReturn(mockUser);

        // Act: last login time should be updated to now
        userService.loginUser("test@test.com", "correct_hash");

        // Assert: last login time should be updated and user should be saved
        assertTrue(mockUser.getLastLogin().isAfter(oldLoginTime));
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void setUserSocialBattery_ValidInput_UpdatesAndSaves() {
        // Arrange
        UUID id = UUID.randomUUID();
        User mockUser = new User(id, "Test User", "hash", "test@test.com", 50, 0, 0, LocalDateTime.now());
        when(userRepository.findById(id)).thenReturn(Optional.of(mockUser));

        // Act
        userService.setUserSocialBattery(id, 75);

        // Assert
        assertEquals(75, mockUser.getSocialBattery());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void setUserSocialBattery_TooLow_ThrowsException() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.setUserSocialBattery(id, -1));
        assertTrue(exception.getMessage().contains("Social battery must be between 0 and 100"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void setUserSocialBattery_TooHigh_ThrowsException() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.setUserSocialBattery(id, 101));
        assertTrue(exception.getMessage().contains("Social battery must be between 0 and 100"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void setUserSocialBattery_NullUserId_ThrowsException() {
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.setUserSocialBattery(null, 50));
        assertEquals("User ID must not be null.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void setUserSocialBattery_UserNotFound_ThrowsException() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.setUserSocialBattery(id, 50));
        assertTrue(exception.getMessage().contains("User not found"));
        verify(userRepository, never()).save(any(User.class));
    }

    // TODO: implement real interests method when interests logic is finished

}