package com.elyashevich.card_manager.service.impl;

import com.elyashevich.card_manager.entity.Card;
import com.elyashevich.card_manager.entity.CardLimit;
import com.elyashevich.card_manager.entity.CardStatus;
import com.elyashevich.card_manager.entity.Transaction;
import com.elyashevich.card_manager.entity.TransactionType;
import com.elyashevich.card_manager.exception.ResourceNotFoundException;
import com.elyashevich.card_manager.exception.TransactionException;
import com.elyashevich.card_manager.repository.TransactionRepository;
import com.elyashevich.card_manager.service.CardLimitService;
import com.elyashevich.card_manager.service.CardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Transaction Service Implementation Tests")
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CardService cardService;

    @Mock
    private CardLimitService cardLimitService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    @Order(1)
    @DisplayName("Find all by card ID should return transactions when card exists and belongs to user")
    void findAllByCardIdShouldReturnTransactionsWhenCardExists() {
        // Arrange
        Long cardId = 1L;
        String userEmail = "user@example.com";
        List<Transaction> expectedTransactions = List.of(
            new Transaction(),
            new Transaction()
        );

        when(cardService.existsByUserEmailAndCardId(userEmail, cardId)).thenReturn(true);
        when(transactionRepository.findAllByCardId(cardId)).thenReturn(expectedTransactions);
        mockSecurityContext(userEmail);

        // Act
        List<Transaction> result = transactionService.findAllByCardId(cardId);

        // Assert
        assertEquals(expectedTransactions, result);
        verify(cardService).existsByUserEmailAndCardId(userEmail, cardId);
        verify(transactionRepository).findAllByCardId(cardId);
    }

    @Test
    @Order(2)
    @DisplayName("Find all by card ID should throw exception when card not found")
    void findAllByCardIdShouldThrowExceptionWhenCardNotFound() {
        // Arrange
        Long cardId = 1L;
        String userEmail = "user@example.com";

        when(cardService.existsByUserEmailAndCardId(userEmail, cardId)).thenReturn(false);
        mockSecurityContext(userEmail);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> transactionService.findAllByCardId(cardId)
        );
        assertEquals(
            String.format(TransactionServiceImpl.CARD_WITH_USER_EMAIL_AND_CARD_ID_WAS_NOT_FOUND_TEMPLATE, userEmail, cardId),
            exception.getMessage()
        );
    }

    @Test
    @Order(3)
    @DisplayName("Find by ID should return transaction when exists")
    void findByIdShouldReturnTransactionWhenExists() {
        // Arrange
        Long transactionId = 1L;
        Transaction expectedTransaction = new Transaction();
        
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(expectedTransaction));

        // Act
        Transaction result = transactionService.findById(transactionId);

        // Assert
        assertEquals(expectedTransaction, result);
        verify(transactionRepository).findById(transactionId);
    }

    @Test
    @Order(4)
    @DisplayName("Find by ID should throw exception when transaction not exists")
    void findByIdShouldThrowExceptionWhenTransactionNotExists() {
        // Arrange
        Long transactionId = 1L;
        
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> transactionService.findById(transactionId)
        );
        assertEquals(
            String.format("Transaction with id: '%s' was not found", transactionId),
            exception.getMessage()
        );
    }

    @Test
    @Order(5)
    @DisplayName("Withdraw from card should complete successfully when valid")
    void withdrawFromCardShouldCompleteWhenValid() {
        // Arrange
        Long cardId = 1L;
        BigDecimal amount = new BigDecimal("100");
        String userEmail = "user@example.com";
        
        Card card = Card.builder()
            .id(cardId)
            .balance(new BigDecimal("500"))
            .status(CardStatus.ACTIVE)
            .limit(CardLimit.builder()
                .dailyLimit(new BigDecimal("1000"))
                .monthlyLimit(new BigDecimal("5000"))
                .build())
            .build();

        when(cardService.findByCardIdAndUserEmail(cardId, userEmail)).thenReturn(card);
        when(transactionRepository.calculateDailySpent(cardId, LocalDate.now())).thenReturn(BigDecimal.ZERO);
        when(transactionRepository.calculateMonthlySpent(cardId, LocalDate.now().getYear(), YearMonth.now().getMonthValue()))
            .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));
        mockSecurityContext(userEmail);

        // Act
        Transaction result = transactionService.withdrawFromCard(cardId, amount);

        // Assert
        assertNotNull(result);
        assertEquals(TransactionType.WITHDRAWAL, result.getType());
        assertEquals(amount, result.getAmount());
        verify(cardService).updateAllBalances(anyList());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @Order(6)
    @DisplayName("Transfer should throw exception when card not active")
    void transferShouldThrowExceptionWhenCardNotActive() {
        // Arrange
        Long fromCardId = 1L;
        Long toCardId = 2L;
        BigDecimal amount = new BigDecimal("100");
        String userEmail = "user@example.com";
        
        Card fromCard = Card.builder()
            .id(fromCardId)
            .status(CardStatus.BLOCKED)
            .build();

        when(cardService.findByCardIdAndUserEmail(fromCardId, userEmail)).thenReturn(fromCard);
        mockSecurityContext(userEmail);

        // Act & Assert
        TransactionException exception = assertThrows(
            TransactionException.class,
            () -> transactionService.transferBetweenCards(fromCardId, toCardId, amount)
        );
        assertEquals("Card status is not ACTIVE", exception.getMessage());
    }

    @Test
    @Order(7)
    @DisplayName("Withdraw should throw exception when insufficient balance")
    void withdrawShouldThrowExceptionWhenInsufficientBalance() {
        // Arrange
        Long cardId = 1L;
        BigDecimal amount = new BigDecimal("1000");
        String userEmail = "user@example.com";
        
        Card card = Card.builder()
            .id(cardId)
            .balance(new BigDecimal("500"))
            .status(CardStatus.ACTIVE)
            .build();

        when(cardService.findByCardIdAndUserEmail(cardId, userEmail)).thenReturn(card);
        mockSecurityContext(userEmail);

        // Act & Assert
        TransactionException exception = assertThrows(
            TransactionException.class,
            () -> transactionService.withdrawFromCard(cardId, amount)
        );
        assertEquals("Card limit not enough", exception.getMessage());
    }

    @Test
    @Order(8)
    @DisplayName("Transfer should throw exception when daily limit exceeded")
    void transferShouldThrowExceptionWhenDailyLimitExceeded() {
        // Arrange
        Long fromCardId = 1L;
        Long toCardId = 2L;
        BigDecimal amount = new BigDecimal("500");
        String userEmail = "user@example.com";
        
        Card fromCard = Card.builder()
            .id(fromCardId)
            .balance(new BigDecimal("1000"))
            .status(CardStatus.ACTIVE)
            .limit(CardLimit.builder()
                .dailyLimit(new BigDecimal("600"))
                .monthlyLimit(new BigDecimal("5000"))
                .build())
            .build();

        when(cardService.findByCardIdAndUserEmail(fromCardId, userEmail)).thenReturn(fromCard);
        when(transactionRepository.calculateDailySpent(fromCardId, LocalDate.now())).thenReturn(new BigDecimal("200"));
        mockSecurityContext(userEmail);

        // Act & Assert
        TransactionException exception = assertThrows(
            TransactionException.class,
            () -> transactionService.transferBetweenCards(fromCardId, toCardId, amount)
        );
        assertEquals("Daily spent limit exceeded", exception.getMessage());
    }

    @Test
    @Order(9)
    @DisplayName("Withdraw should throw exception when monthly limit exceeded")
    void withdrawShouldThrowExceptionWhenMonthlyLimitExceeded() {
        // Arrange
        Long cardId = 1L;
        BigDecimal amount = new BigDecimal("500");
        String userEmail = "user@example.com";
        
        Card card = Card.builder()
            .id(cardId)
            .balance(new BigDecimal("1000"))
            .status(CardStatus.ACTIVE)
            .limit(CardLimit.builder()
                .dailyLimit(new BigDecimal("1000"))
                .monthlyLimit(new BigDecimal("2000"))
                .build())
            .build();

        when(cardService.findByCardIdAndUserEmail(cardId, userEmail)).thenReturn(card);
        when(transactionRepository.calculateDailySpent(cardId, LocalDate.now())).thenReturn(BigDecimal.ZERO);
        when(transactionRepository.calculateMonthlySpent(cardId, LocalDate.now().getYear(), YearMonth.now().getMonthValue()))
            .thenReturn(new BigDecimal("1600"));
        mockSecurityContext(userEmail);

        // Act & Assert
        TransactionException exception = assertThrows(
            TransactionException.class,
            () -> transactionService.withdrawFromCard(cardId, amount)
        );
        assertEquals("Monthly limit exceeded", exception.getMessage());
    }

    private void mockSecurityContext(String email) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}