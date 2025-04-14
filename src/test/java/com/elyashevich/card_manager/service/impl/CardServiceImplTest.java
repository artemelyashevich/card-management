package com.elyashevich.card_manager.service.impl;

import com.elyashevich.card_manager.entity.Card;
import com.elyashevich.card_manager.exception.ResourceNotFoundException;
import com.elyashevich.card_manager.repository.CardRepository;
import com.elyashevich.card_manager.service.EncryptionService;
import com.elyashevich.card_manager.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.annotation.Order;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Card Service Implementation Tests")
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserService userService;

    @Mock
    private EncryptionService encryptionService;

    @InjectMocks
    private CardServiceImpl cardService;

    @Test
    @Order(1)
    @DisplayName("Find by ID should return card when card exists")
    void findByIdShouldReturnCardWhenCardExists() {
        // Arrange
        Long cardId = 1L;
        Card expectedCard = Card.builder().id(cardId).build();
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(expectedCard));

        // Act
        Card result = cardService.findById(cardId);

        // Assert
        assertEquals(expectedCard, result);
        verify(cardRepository).findById(cardId);
    }

    @Test
    @Order(2)
    @DisplayName("Find by ID should throw ResourceNotFoundException when card not exists")
    void findByIdShouldThrowResourceNotFoundExceptionWhenCardNotExists() {
        // Arrange
        Long cardId = 1L;
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> cardService.findById(cardId)
        );
        assertEquals(
            String.format(CardServiceImpl.CARD_WITH_ID_WAS_NOT_FOUND_TEMPLATE, cardId),
            exception.getMessage()
        );
        verify(cardRepository).findById(cardId);
    }

    @Test
    @Order(3)
    @DisplayName("Find by card ID and user email should return card when exists")
    void findByCardIdAndUserEmailShouldReturnCardWhenExists() {
        // Arrange
        Long cardId = 1L;
        String email = "test@example.com";
        Card expectedCard = Card.builder().id(cardId).cardHolderName(email).build();
        
        when(cardRepository.findByIdAndCardHolderName(cardId, email))
            .thenReturn(Optional.of(expectedCard));

        // Act
        Card result = cardService.findByCardIdAndUserEmail(cardId, email);

        // Assert
        assertEquals(expectedCard, result);
        verify(cardRepository).findByIdAndCardHolderName(cardId, email);
    }

    @Test
    @Order(4)
    @DisplayName("Find by card ID and user email should throw exception when not exists")
    void findByCardIdAndUserEmailShouldThrowExceptionWhenNotExists() {
        // Arrange
        Long cardId = 1L;
        String email = "test@example.com";
        
        when(cardRepository.findByIdAndCardHolderName(cardId, email))
            .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> cardService.findByCardIdAndUserEmail(cardId, email)
        );
        assertEquals(
            String.format(CardServiceImpl.CARD_WITH_ID_AND_USER_EMAIL_WAS_NOT_FOUND_TEMPLATE, cardId, email),
            exception.getMessage()
        );
        verify(cardRepository).findByIdAndCardHolderName(cardId, email);
    }

    @Test
    @Order(5)
    @DisplayName("Delete should remove card when exists")
    void deleteShouldRemoveCardWhenExists() {
        // Arrange
        Long cardId = 1L;
        Card existingCard = Card.builder().id(cardId).build();
        
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(existingCard));
        doNothing().when(cardRepository).delete(existingCard);

        // Act
        cardService.delete(cardId);

        // Assert
        verify(cardRepository).findById(cardId);
        verify(cardRepository).delete(existingCard);
    }

    @Test
    @Order(6)
    @DisplayName("Exists by user email and card ID should return true when exists")
    void existsByUserEmailAndCardIdShouldReturnTrueWhenExists() {
        // Arrange
        Long cardId = 1L;
        String email = "test@example.com";
        
        when(cardRepository.existsByIdAndUserEmail(cardId, email)).thenReturn(true);

        // Act
        boolean result = cardService.existsByUserEmailAndCardId(email, cardId);

        // Assert
        assertTrue(result);
        verify(cardRepository).existsByIdAndUserEmail(cardId, email);
    }
}