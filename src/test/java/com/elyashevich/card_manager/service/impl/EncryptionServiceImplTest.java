package com.elyashevich.card_manager.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.annotation.Order;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@DisplayName("Encryption Service Implementation Tests")
class EncryptionServiceImplTest {

    private EncryptionServiceImpl encryptionService;

    @BeforeEach
    void setUp() {
        encryptionService = new EncryptionServiceImpl();
    }

    @Test
    @Order(1)
    @DisplayName("Encrypt should return encrypted bytes when valid input")
    void encryptShouldReturnEncryptedBytesWhenValidInput() throws Exception {
        // Arrange
        String plainText = "4111111111111111";

        // Act
        byte[] result = encryptionService.encrypt(plainText);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertNotEquals(plainText, new String(result));
    }

    @Test
    @Order(2)
    @DisplayName("Decrypt should return original string when valid encrypted bytes")
    void decryptShouldReturnOriginalStringWhenValidEncryptedBytes() throws Exception {
        // Arrange
        String originalText = "4111111111111111";
        byte[] encrypted = encryptionService.encrypt(originalText);

        // Act
        String result = encryptionService.decrypt(encrypted);

        // Assert
        assertEquals(originalText, result);
    }

    @Test
    @Order(3)
    @DisplayName("Encrypt and decrypt should be reversible operations")
    void encryptAndDecryptShouldBeReversible() throws Exception {
        // Arrange
        String originalText = "test123456789012";

        // Act
        byte[] encrypted = encryptionService.encrypt(originalText);
        String decrypted = encryptionService.decrypt(encrypted);

        // Assert
        assertEquals(originalText, decrypted);
    }

    @ParameterizedTest
    @Order(4)
    @DisplayName("Encrypt should handle different input lengths")
    @ValueSource(strings = {"", "1", "1234567890123456", "longer test string 1234567890"})
    void encryptShouldHandleDifferentInputLengths(String input) throws Exception {
        // Act
        byte[] encrypted = encryptionService.encrypt(input);
        String decrypted = encryptionService.decrypt(encrypted);

        // Assert
        assertEquals(input, decrypted);
    }

    @Test
    @Order(5)
    @DisplayName("Decrypt should throw IllegalBlockSizeException when invalid block size")
    void decryptShouldThrowIllegalBlockSizeExceptionWhenInvalidBlockSize() {
        // Arrange
        byte[] invalidBlockSize = new byte[1]; // Too short for AES

        // Act & Assert
        assertThrows(IllegalBlockSizeException.class, () -> encryptionService.decrypt(invalidBlockSize));
    }
}