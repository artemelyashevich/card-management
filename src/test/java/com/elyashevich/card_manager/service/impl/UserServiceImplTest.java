package com.elyashevich.card_manager.service.impl;

import com.elyashevich.card_manager.entity.User;
import com.elyashevich.card_manager.exception.ResourceAlreadyExistsException;
import com.elyashevich.card_manager.exception.ResourceNotFoundException;
import com.elyashevich.card_manager.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Implementation Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @Order(1)
    @DisplayName("Find by ID should return user when user exists")
    void findByIdShouldReturnUserWhenUserExists() {
        // Arrange
        Long userId = 1L;
        User expectedUser = User.builder().id(userId).email("test@example.com").build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        // Act
        User result = userService.findById(userId);

        // Assert
        assertEquals(expectedUser, result);
        verify(userRepository).findById(userId);
    }

    @Test
    @Order(2)
    @DisplayName("Find by ID should throw ResourceNotFoundException when user not exists")
    void findByIdShouldThrowResourceNotFoundExceptionWhenUserNotExists() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> userService.findById(userId));

        assertEquals("User with id: '1' was not found", exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    @Order(3)
    @DisplayName("Find by email should return user when user exists")
    void findByEmailShouldReturnUserWhenUserExists() {
        // Arrange
        String email = "test@example.com";
        User expectedUser = User.builder().email(email).build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(expectedUser));

        // Act
        User result = userService.findByEmail(email);

        // Assert
        assertEquals(expectedUser, result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    @Order(4)
    @DisplayName("Find by email should throw ResourceNotFoundException when user not exists")
    void findByEmailShouldThrowResourceNotFoundExceptionWhenUserNotExists() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> userService.findByEmail(email));

        assertEquals("User with email: 'nonexistent@example.com' was not found.", exception.getMessage());
        verify(userRepository).findByEmail(email);
    }

    @Test
    @Order(5)
    @DisplayName("Create should throw ResourceAlreadyExistsException when email exists")
    void createShouldThrowResourceAlreadyExistsExceptionWhenEmailExists() {
        // Arrange
        String email = "existing@example.com";
        User newUser = User.builder().email(email).password("password").build();
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Act & Assert
        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class,
            () -> userService.create(newUser));

        assertEquals("User with email: 'existing@example.com' already exists", exception.getMessage());
        verify(userRepository).existsByEmail(email);
        verify(userRepository, never()).save(any());
    }

    @ParameterizedTest
    @CsvSource({
        "old@test.com, new@test.com",
        "user@example.com, updated@example.com"
    })
    @Order(6)
    @DisplayName("Update email should update email when new email is unique")
    void updateEmailShouldUpdateEmailWhenNewEmailIsUnique(String originalEmail, String newEmail) {
        // Arrange
        Long userId = 1L;
        User existingUser = User.builder().id(userId).email(originalEmail).build();
        User updatedUser = User.builder().email(newEmail).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail(newEmail)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = userService.updateEmail(userId, updatedUser);

        // Assert
        assertEquals(newEmail, result.getEmail());
        verify(userRepository).existsByEmail(newEmail);
        verify(userRepository).save(existingUser);
    }

    @Test
    @Order(7)
    @DisplayName("Update email should not check email when email not changed")
    void updateEmailShouldNotCheckEmailWhenEmailNotChanged() {
        // Arrange
        Long userId = 1L;
        String email = "same@example.com";
        User existingUser = User.builder().id(userId).email(email).build();
        User updatedUser = User.builder().email(email).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = userService.updateEmail(userId, updatedUser);

        // Assert
        assertEquals(email, result.getEmail());
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository).save(existingUser);
    }

    @ParameterizedTest
    @ValueSource(strings = {"duplicate1@test.com", "duplicate2@test.com"})
    @Order(8)
    @DisplayName("Update email should throw ResourceAlreadyExistsException when new email exists")
    void updateEmailShouldThrowResourceAlreadyExistsExceptionWhenNewEmailExists(String duplicateEmail) {
        // Arrange
        Long userId = 1L;
        User existingUser = User.builder().id(userId).email("original@test.com").build();
        User updatedUser = User.builder().email(duplicateEmail).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail(duplicateEmail)).thenReturn(true);

        // Act & Assert
        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class,
            () -> userService.updateEmail(userId, updatedUser));

        assertEquals(String.format("User with email: '%s' already exists", duplicateEmail), exception.getMessage());
        verify(userRepository).existsByEmail(duplicateEmail);
        verify(userRepository, never()).save(any());
    }

    @Test
    @Order(9)
    @DisplayName("Delete should delete user when user exists")
    void deleteShouldDeleteUserWhenUserExists() {
        // Arrange
        Long userId = 1L;
        User user = User.builder().id(userId).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        // Act
        userService.delete(userId);

        // Assert
        verify(userRepository).findById(userId);
        verify(userRepository).delete(user);
    }

    @Test
    @Order(10)
    @DisplayName("Delete should throw ResourceNotFoundException when user not exists")
    void deleteShouldThrowResourceNotFoundExceptionWhenUserNotExists() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.delete(userId));
        verify(userRepository, never()).delete(any());
    }
}