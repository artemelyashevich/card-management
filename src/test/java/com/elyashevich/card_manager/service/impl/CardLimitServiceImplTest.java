package com.elyashevich.card_manager.service.impl;

import com.elyashevich.card_manager.entity.Card;
import com.elyashevich.card_manager.entity.CardLimit;
import com.elyashevich.card_manager.exception.ResourceNotFoundException;
import com.elyashevich.card_manager.repository.CardLimitRepository;
import com.elyashevich.card_manager.service.CardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Card Limit Service Implementation Tests")
class CardLimitServiceImplTest {

    @Mock
    private CardService cardService;

    @Mock
    private CardLimitRepository cardLimitRepository;

    @InjectMocks
    private CardLimitServiceImpl cardLimitService;

    @Test
    @Order(1)
    @DisplayName("Set limit should save limit and update card when valid input")
    void setLimitShouldSaveLimitAndUpdateCardWhenValidInput() {
        // Arrange
        Long cardId = 1L;
        CardLimit limit = CardLimit.builder()
            .dailyLimit(new BigDecimal("1000"))
            .monthlyLimit(new BigDecimal("5000"))
            .build();
        
        CardLimit savedLimit = CardLimit.builder()
            .id(2L)
            .dailyLimit(limit.getDailyLimit())
            .monthlyLimit(limit.getMonthlyLimit())
            .build();
        
        Card expectedCard = Card.builder()
            .id(cardId)
            .limit(savedLimit)
            .build();

        when(cardLimitRepository.save(limit)).thenReturn(savedLimit);
        when(cardService.saveLimit(cardId, savedLimit)).thenReturn(expectedCard);

        // Act
        Card result = cardLimitService.setLimit(cardId, limit);

        // Assert
        assertNotNull(result);
        assertEquals(cardId, result.getId());
        assertEquals(savedLimit, result.getLimit());
        
        verify(cardLimitRepository).save(limit);
        verify(cardService).saveLimit(cardId, savedLimit);
        verifyNoMoreInteractions(cardLimitRepository, cardService);
    }


    @Test
    @Order(2)
    @DisplayName("Delete limit should delete limit when it exists")
    void deleteLimitShouldDeleteLimitWhenItExists() {
        // Arrange
        Long cardId = 1L;
        Long limitId = 1L;

        CardLimit existingLimit = CardLimit.builder()
            .id(limitId)
            .build();

        when(cardLimitRepository.findById(cardId)).thenReturn(Optional.of(existingLimit));
        doNothing().when(cardService).deleteLimit(cardId);
        doNothing().when(cardLimitRepository).delete(existingLimit);

        // Act
        cardLimitService.deleteLimit(cardId, limitId);

        // Assert
        verify(cardLimitRepository).findById(cardId);
        verify(cardService).deleteLimit(cardId);
        verify(cardLimitRepository).delete(existingLimit);
        verifyNoMoreInteractions(cardLimitRepository, cardService);
    }

    @Test
    @Order(3)
    @DisplayName("Delete limit should throw ResourceNotFoundException when limit not exists")
    void deleteLimitShouldThrowResourceNotFoundExceptionWhenLimitNotExists() {
        // Arrange
        Long cardId = 1L;
        Long limitId = 1L;

        when(cardLimitRepository.findById(cardId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> cardLimitService.deleteLimit(cardId, limitId)
        );

        assertEquals(
            String.format(CardLimitServiceImpl.CARD_LIMIT_WITH_ID_WAS_NOT_FOUND_TEMPLATE, limitId),
            exception.getMessage()
        );

        verify(cardLimitRepository).findById(cardId);
        verifyNoInteractions(cardService);
        verifyNoMoreInteractions(cardLimitRepository);
    }
    @Test
    @Order(4)
    @DisplayName("Set limit should propagate transaction with REQUIRES_NEW")
    void setLimitShouldHaveCorrectTransactionalAnnotation() throws NoSuchMethodException {
        // Arrange
        Method method = CardLimitServiceImpl.class.getMethod("setLimit", Long.class, CardLimit.class);
        Transactional transactional = method.getAnnotation(Transactional.class);

        // Assert
        assertNotNull(transactional);
        assertEquals(Propagation.REQUIRES_NEW, transactional.propagation());
    }
}