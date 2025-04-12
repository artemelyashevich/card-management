package com.elyashevich.card_manager.service.impl;

import com.elyashevich.card_manager.entity.User;
import com.elyashevich.card_manager.exception.PasswordMismatchException;
import com.elyashevich.card_manager.exception.ResourceAlreadyExistsException;
import com.elyashevich.card_manager.exception.ResourceNotFoundException;
import com.elyashevich.card_manager.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Service Implementation Tests")
class AuthServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private static Stream<Arguments> provideTestUsers() {
        return Stream.of(
            Arguments.of(User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .build()),
            Arguments.of(User.builder()
                .email("user@test.com")
                .password("encoded123")
                .build()),
            Arguments.of(User.builder()
                .email("admin@example.com")
                .password("adminEncoded")
                .build())
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestUsers")
    @Order(1)
    @DisplayName("Authenticate should return user with valid credentials")
    void authenticateShouldReturnUserWhenCredentialsValid(User testUser) {
        // Arrange
        User inputUser = User.builder()
            .email(testUser.getEmail())
            .password("rawPassword")
            .build();

        when(userService.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(passwordEncoder.matches("rawPassword", testUser.getPassword())).thenReturn(true);

        // Act
        User result = authService.authenticate(inputUser);

        // Assert
        assertEquals(testUser, result);
        verify(userService).findByEmail(testUser.getEmail());
        verify(passwordEncoder).matches("rawPassword", testUser.getPassword());
    }

    @ParameterizedTest
    @MethodSource("provideTestUsers")
    @Order(2)
    @DisplayName("Authenticate should throw PasswordMismatchException for invalid passwords")
    void authenticateShouldThrowExceptionWhenPasswordInvalid(User testUser) {
        // Arrange
        User inputUser = User.builder()
            .email(testUser.getEmail())
            .password("wrongPassword")
            .build();

        when(userService.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(passwordEncoder.matches("wrongPassword", testUser.getPassword())).thenReturn(false);

        // Act & Assert
        PasswordMismatchException exception = assertThrows(
            PasswordMismatchException.class,
            () -> authService.authenticate(inputUser)
        );

        assertEquals("Invalid password", exception.getMessage());
        verify(userService).findByEmail(testUser.getEmail());
        verify(passwordEncoder).matches("wrongPassword", testUser.getPassword());
    }

    @ParameterizedTest
    @CsvSource({
        "nonexistent@example.com, encodedPass1",
        "unknown@test.com, encodedPass2",
        "deleted@user.com, encodedPass3"
    })
    @Order(3)
    @DisplayName("Authenticate should propagate ResourceNotFoundException for non-existent users")
    void authenticateShouldPropagateUserNotFoundWhenUserNotExists(String nonExistentEmail, String password) {
        // Arrange
        User inputUser = User.builder()
            .email(nonExistentEmail)
            .password(password)
            .build();

        when(userService.findByEmail(nonExistentEmail))
            .thenThrow(new ResourceNotFoundException("User not found"));

        // Act & Assert
        assertThrows(
            ResourceNotFoundException.class,
            () -> authService.authenticate(inputUser)
        );

        verify(userService).findByEmail(nonExistentEmail);
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @ParameterizedTest
    @MethodSource("provideTestUsers")
    @Order(4)
    @DisplayName("Register should return created user for new email")
    void registerShouldReturnUserWhenEmailNew(User testUser) {
        // Arrange
        User newUser = User.builder()
            .email(testUser.getEmail())
            .password("rawPassword")
            .build();

        when(userService.create(newUser)).thenReturn(testUser);

        // Act
        User result = authService.register(newUser);

        // Assert
        assertEquals(testUser, result);
        verify(userService).create(newUser);
    }

    @ParameterizedTest
    @CsvSource({
        "existing@example.com, password123",
        "duplicate@test.com, qwerty",
        "registered@user.com, 123456"
    })
    @Order(5)
    @DisplayName("Register should throw ResourceAlreadyExistsException for existing emails")
    void registerShouldThrowExceptionWhenEmailExists(String existingEmail, String password) {
        // Arrange
        User existingUser = User.builder()
            .email(existingEmail)
            .password(password)
            .build();

        when(userService.create(existingUser))
            .thenThrow(new ResourceAlreadyExistsException("Email already exists"));

        // Act & Assert
        assertThrows(
            ResourceAlreadyExistsException.class,
            () -> authService.register(existingUser)
        );

        verify(userService).create(existingUser);
    }
}