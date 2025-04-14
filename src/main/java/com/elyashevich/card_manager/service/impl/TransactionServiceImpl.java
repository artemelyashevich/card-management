package com.elyashevich.card_manager.service.impl;

import com.elyashevich.card_manager.entity.Card;
import com.elyashevich.card_manager.entity.CardStatus;
import com.elyashevich.card_manager.entity.Transaction;
import com.elyashevich.card_manager.entity.TransactionType;
import com.elyashevich.card_manager.exception.ResourceNotFoundException;
import com.elyashevich.card_manager.exception.TransactionException;
import com.elyashevich.card_manager.repository.TransactionRepository;
import com.elyashevich.card_manager.service.CardLimitService;
import com.elyashevich.card_manager.service.CardService;
import com.elyashevich.card_manager.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    public static final String CARD_WITH_USER_EMAIL_AND_CARD_ID_WAS_NOT_FOUND_TEMPLATE = "Card with user email '%s' " +
        "and cardId '%d' was not found.";
    private final TransactionRepository transactionRepository;
    private final CardService cardService;
    private final CardLimitService cardLimitService;

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findAllByCardId(final Long cardId) {
        log.debug("Attempting find all transactions by cardId: {}", cardId);

        var userEmail = this.getUserEmail();

        if (!this.cardService.existsByUserEmailAndCardId(userEmail, cardId)) {
            throw new ResourceNotFoundException(CARD_WITH_USER_EMAIL_AND_CARD_ID_WAS_NOT_FOUND_TEMPLATE
                .formatted(userEmail, cardId));
        }

        var transactions = this.transactionRepository.findAllByCardId(cardId);

        log.info("Found {} transactions", transactions.size());
        return transactions;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Transaction transferBetweenCards(final Long fromCardId, final Long toCardId, final BigDecimal amount) {
        log.debug("Attempting transfer between cards from {} to {}", fromCardId, toCardId);

        var userEmail = this.getUserEmail();

        var fromCard = this.cardService.findByCardIdAndUserEmail(fromCardId, userEmail);
        var toCard = this.cardService.findByCardIdAndUserEmail(toCardId, userEmail);

        validateTransaction(fromCard, amount);

        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        toCard.setBalance(toCard.getBalance().add(amount));

        // saveAllCards

        var transaction =
            this.transactionRepository.save(Transaction.builder()
                .amount(amount)
                .type(TransactionType.TRANSFER)
                .card(fromCard)
                .timestamp(LocalDateTime.now())
                .build()
        );

        log.info("Transfer between cards from {} to {}", fromCardId, toCardId);
        return transaction;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Transaction withdrawFromCard(final Long cardId, final BigDecimal amount) {
        log.debug("Attempting withdraw from card: {}", cardId);

        var userEmail = this.getUserEmail();
        var card = this.cardService.findByCardIdAndUserEmail(cardId, userEmail);

        validateTransaction(card, amount);

        card.setBalance(card.getBalance().subtract(amount));

        // save card

        var transaction = this.transactionRepository.save(Transaction.builder()
            .card(card)
            .amount(amount)
            .timestamp(LocalDateTime.now())
            .type(TransactionType.WITHDRAWAL)
            .build());

        log.info("Withdraw from card: {}", cardId);
        return transaction;
    }

    private void validateTransaction(final Card card, final BigDecimal amount) {
        if (!card.getStatus().equals(CardStatus.ACTIVE)) {
            throw new TransactionException("Card status is not ACTIVE");
        }

        if (card.getBalance().compareTo(amount) < 0) {
            throw new TransactionException("Card limit not enough");
        }

        var cardLimit = card.getLimit();

        var dailySpent = this.transactionRepository.calculateDailySpent(card.getId(), LocalDate.now());
        if (dailySpent.add(amount).compareTo(cardLimit.getDailyLimit()) > 0) {
            throw new TransactionException("Daily spent limit exceeded");
        }

        var monthlySpend = this.transactionRepository.calculateMonthlySpent(
            card.getId(),
            LocalDate.now().getYear(),
            YearMonth.now().getMonthValue()
        );
        if (monthlySpend.add(amount).compareTo(cardLimit.getMonthlyLimit()) > 0) {
            throw new TransactionException("Monthly limit exceeded");
        }
    }

    private String getUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
