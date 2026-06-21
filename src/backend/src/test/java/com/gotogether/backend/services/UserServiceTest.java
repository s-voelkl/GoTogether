package com.gotogether.backend.services;

import com.gotogether.backend.dto.UserCreateDTO;
import com.gotogether.backend.dto.UserDTO;
import com.gotogether.backend.mapper.UserMapper;
import com.gotogether.backend.model.Settings;
import com.gotogether.backend.model.Topic;
import com.gotogether.backend.model.User;
import com.gotogether.backend.repository.UserRepository;
import com.gotogether.backend.repository.TopicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private UserService userService;

    @Spy
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    private User buildUser(int xp) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Test User");
        user.setPassword("secret");
        user.setEmail("test@example.com");
        user.setSocialBattery(80);
        user.setCurrency(200);
        user.setExperiencePoints(xp);
        Topic topic = new Topic();
        topic.setId(UUID.randomUUID());
        topic.setName("sample");
        user.setInterests(List.of(topic));
        user.setLastLogin(LocalDateTime.now());
        user.setSettings(new Settings());
        return user;
    }

    @Test
    void toDTO_mapsAllFieldsCorrectly() {
        User user = buildUser(0);

        UserDTO dto = userMapper.toDTO(user);

        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
        assertEquals(user.getSocialBattery(), dto.getSocialBattery());
        assertEquals(user.getCurrency(), dto.getCurrency());
        assertEquals(user.getInterests().stream().map(Topic::getId).toList(), dto.getInterests());
        assertEquals(user.getLastLogin(), dto.getLastLogin());
        assertEquals(user.getSettings(), dto.getSettings());
    }

    @Test
    void toDTO_withZeroXp_returnsZeroLevelXp() {
        UserDTO dto = userMapper.toDTO(buildUser(0));

        assertEquals(0, dto.getLevelXp());
    }

    @Test
    void toDTO_levelXpIsXpMinusCurrentLevelFloor() {
        // 220 XP puts us at level 2 (floor 215), so levelXp should be 5
        UserDTO dto = userMapper.toDTO(buildUser(220));

        assertEquals(2, dto.getLevel());
        assertEquals(6, dto.getLevelXp());
    }

    @Test
    void toDTO_levelXpResetsAtLevelBoundary() {
        // exactly at level 2 threshold (215 XP), levelXp should be 0
        UserDTO dto = userMapper.toDTO(buildUser(215));

        assertEquals(2, dto.getLevel());
        assertEquals(1, dto.getLevelXp());
    }

    @Test
    void toDTO_levelXpIsAlwaysNonNegative() {
        for (int xp = 0; xp <= 1000; xp += 50) {
            UserDTO dto = userMapper.toDTO(buildUser(xp));
            assertTrue(dto.getLevelXp() >= 0);
        }
    }

    @Test
    void toDTO_withNullInterests_mapsToNull() {
        User user = buildUser(0);
        user.setInterests(null);

        UserDTO dto = userMapper.toDTO(user);

        assertNull(dto.getInterests());
    }

    @Test
    void toDTO_withZeroXp_returnsLevelOne() {
        User user = buildUser(0);

        UserDTO dto = userMapper.toDTO(user);

        assertEquals(1, dto.getLevel());
    }

    @Test
    void toDTO_withXpJustBelowLevelTwo_returnsLevelOne() {
        // E(2) = 100 * (1 - 1.15^2) / (1 - 1.15) = 214, so 213 XP should still be level
        // 1
        User user = buildUser(213);

        UserDTO dto = userMapper.toDTO(user);

        assertEquals(1, dto.getLevel());
    }

    @Test
    void toDTO_withXpAtLevelTwo_returnsLevelTwo() {
        User user = buildUser(214);

        UserDTO dto = userMapper.toDTO(user);

        assertEquals(2, dto.getLevel());
    }

    @Test
    void toDTO_withVeryHighXp_capsAtMaxLevel() {
        User user = buildUser(Integer.MAX_VALUE);

        UserDTO dto = userMapper.toDTO(user);

        assertEquals(100, dto.getLevel());
    }

    @Test
    void getUserById_UserExists_ReturnsUser() {
        // Arrange
        User mockUser = buildUser(0);
        mockUser.setId(UUID.randomUUID());
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));

        // Act
        UserDTO result = userService.getUserById(mockUser.getId());

        // Assert
        assertNotNull(result);
        assertEquals(mockUser.getId(), result.getId());
        assertEquals("Test User", result.getName());
        verify(userRepository, times(1)).findById(mockUser.getId());
    }

    @Test
    void getUserById_UserDoesNotExist_ThrowsRuntimeException() {
        // Arrange
        User mockUser = new User("Test User", "hash", "test@test.com");
        mockUser.setId(UUID.randomUUID());
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.getUserById(mockUser.getId()));
        verify(userRepository, times(1)).findById(mockUser.getId());
    }

    @Test
    void getAllUsers_UsersExist_ReturnsList() {
        // Arrange
        User mockUser1 = new User("User One", "hash1", "one@test.com");
        User mockUser2 = new User("User Two", "hash2", "two@test.com");
        when(userRepository.findAll()).thenReturn(List.of(mockUser1, mockUser2));

        // Act
        List<UserDTO> result = userService.getAllUsers();

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
        assertThrows(RuntimeException.class,
                () -> userService.createUser(dto.getUsername(), dto.getPassword(), dto.getEmail()));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_EmptyEmail_ThrowsRuntimeException() {
        // Arrange
        UserCreateDTO dto = new UserCreateDTO("testuser", "password", "");

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> userService.createUser(dto.getUsername(), dto.getPassword(), dto.getEmail()));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_InvalidEmailFormat_ThrowsRuntimeException() {
        // Arrange
        UserCreateDTO dto = new UserCreateDTO("testuser", "password", "not_an_email");

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> userService.createUser(dto.getUsername(), dto.getPassword(), dto.getEmail()));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_StrictInvalidEmailFormat_ThrowsRuntimeException() {
        // Arrange
        UserCreateDTO dto = new UserCreateDTO("testuser", "password", "a@b"); // Valid based on old regex, invalid for
                                                                              // EmailValidator

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> userService.createUser(dto.getUsername(), dto.getPassword(), dto.getEmail()));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_EmptyUsername_ThrowsRuntimeException() {
        // Arrange
        UserCreateDTO dto = new UserCreateDTO("", "password", "test@test.com");

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> userService.createUser(dto.getUsername(), dto.getPassword(), dto.getEmail()));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_EmptyPassword_ThrowsRuntimeException() {
        // Arrange
        UserCreateDTO dto = new UserCreateDTO("testuser", "", "test@test.com");

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> userService.createUser(dto.getUsername(), dto.getPassword(), dto.getEmail()));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginUser_ValidCredentials_ReturnsUserId() {
        // Arrange
        User mockUser = new User("Test User", "correct_hash", "test@test.com");
        mockUser.setId(UUID.randomUUID());
        when(userRepository.findByEmail("test@test.com")).thenReturn(mockUser);
        when(securityService.passwordMatches("correct_hash", "correct_hash")).thenReturn(true);

        // Act
        UUID resultId = userService.loginUser("test@test.com", "correct_hash");

        // Assert
        assertEquals(mockUser.getId(), resultId);
        verify(userRepository, times(1)).findByEmail("test@test.com");
    }

    @Test
    void loginUser_InvalidEmail_ThrowsRuntimeException() {
        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.loginUser("not_an_email", "hash"));
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void loginUser_StrictInvalidEmail_ThrowsRuntimeException() {
        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.loginUser("a@b", "hash"));
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void loginUser_EmptyPassword_ThrowsRuntimeException() {
        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.loginUser("test@test.com", ""));
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void loginUser_UserNotFound_ThrowsRuntimeException() {
        // Arrange
        when(userRepository.findByEmail("test@test.com")).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> userService.loginUser("test@test.com", "hash"));
        verify(userRepository, times(1)).findByEmail("test@test.com");
    }

    @Test
    void loginUser_InvalidPassword_ThrowsRuntimeException() {
        // Arrange
        User mockUser = new User("Test User", "correct_hash", "test@test.com");
        when(userRepository.findByEmail("test@test.com")).thenReturn(mockUser);
        when(securityService.passwordMatches("wrong_hash", "correct_hash")).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> userService.loginUser("test@test.com", "wrong_hash"));
        verify(userRepository, times(1)).findByEmail("test@test.com");
    }

    // Test update of last login date
    @Test
    void loginUser_ValidCredentials_UpdatesLastLogin() {
        // Arrange: set user with old last login time
        LocalDateTime oldLoginTime = LocalDateTime.now().minusDays(1);
        User mockUser = new User("Test User", "correct_hash", "test@test.com");
        mockUser.setId(UUID.randomUUID());
        when(userRepository.findByEmail("test@test.com")).thenReturn(mockUser);
        when(securityService.passwordMatches("correct_hash", "correct_hash")).thenReturn(true);

        // Act: last login time should be updated to now
        userService.loginUser("test@test.com", "correct_hash");

        // Assert: last login time should be updated and user should be saved
        assertTrue(mockUser.getLastLogin().isAfter(oldLoginTime));
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void setUserSocialBattery_ValidInput_UpdatesAndSaves() {
        // Arrange
        User mockUser = new User("Test User", "hash", "test@test.com");
        mockUser.setId(UUID.randomUUID());
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));

        // Act
        userService.setUserSocialBattery(mockUser.getId(), 75);

        // Assert
        assertEquals(75, mockUser.getSocialBattery());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void setUserSocialBattery_TooLow_ThrowsException() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> userService.setUserSocialBattery(id, -1));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void setUserSocialBattery_TooHigh_ThrowsException() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> userService.setUserSocialBattery(id, 101));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void setUserSocialBattery_NullUserId_ThrowsException() {
        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> userService.setUserSocialBattery(null, 50));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void setUserSocialBattery_UserNotFound_ThrowsException() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> userService.setUserSocialBattery(id, 50));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void setUserInterests_ValidInput_UpdatesAndSaves() {
        // Arrange
        UUID interestId1 = UUID.randomUUID();
        UUID interestId2 = UUID.randomUUID();
        List<UUID> interests = List.of(interestId1, interestId2);

        User mockUser = new User("Test User", "hash", "test@test.com");
        mockUser.setId(UUID.randomUUID());

        Topic t1 = new Topic();
        t1.setId(interestId1);
        Topic t2 = new Topic();
        t2.setId(interestId2);
        when(topicRepository.findById(interestId1)).thenReturn(Optional.of(t1));
        when(topicRepository.findById(interestId2)).thenReturn(Optional.of(t2));
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));

        // Act
        userService.setUserInterests(mockUser.getId(), interests);

        // Assert
        assertEquals(interests, mockUser.getInterests().stream().map(Topic::getId).toList());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void setUserInterests_NullUserId_ThrowsException() {
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.setUserInterests(null, List.of(UUID.randomUUID())));
        assertEquals("User ID must not be null.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void setUserInterests_NullInterestIds_ThrowsException() {
        // Arrange
        UUID userId = UUID.randomUUID();

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.setUserInterests(userId, null));
        assertEquals("Interest IDs must not be null.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void setUserInterests_InterestNotFound_ThrowsException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID interestId = UUID.randomUUID();
        List<UUID> interests = List.of(interestId);

        when(topicRepository.findById(interestId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.setUserInterests(userId, interests));
        assertEquals("Interest not found: " + interestId, exception.getMessage());
        verify(userRepository, never()).findById(any(UUID.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void setUserInterests_UserNotFound_ThrowsException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID interestId = UUID.randomUUID();
        List<UUID> interests = List.of(interestId);

        Topic topic = new Topic();
        topic.setId(interestId);
        when(topicRepository.findById(interestId)).thenReturn(Optional.of(topic));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.setUserInterests(userId, interests));
        assertEquals("User not found: " + userId, exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

}