package com.elyashevich.card_manager.service.impl;

import com.elyashevich.card_manager.api.dto.card.CardRequestDto;
import com.elyashevich.card_manager.api.dto.card.CardWithUserDto;
import com.elyashevich.card_manager.entity.Card;
import com.elyashevich.card_manager.entity.CardLimit;
import com.elyashevich.card_manager.entity.CardStatus;
import com.elyashevich.card_manager.exception.BusinessException;
import com.elyashevich.card_manager.exception.ResourceNotFoundException;
import com.elyashevich.card_manager.repository.CardRepository;
import com.elyashevich.card_manager.service.CardService;
import com.elyashevich.card_manager.service.EncryptionService;
import com.elyashevich.card_manager.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    public static final String CARD_WITH_ID_WAS_NOT_FOUND_TEMPLATE = "Card with id: '%d' was not found";
    public static final String CARD_WITH_ID_AND_USER_EMAIL_WAS_NOT_FOUND_TEMPLATE = "Card with id: '%d' and userEmail: '%s' was not found";
    private final CardRepository cardRepository;
    private final UserService userService;
    private final EncryptionService encryptionService;

    @Override
    public Card findById(final Long id) {
        log.debug("Attempting find card by id: {}", id);

        var card = this.cardRepository.findById(id).orElseThrow(
            () -> {
                var message = CARD_WITH_ID_WAS_NOT_FOUND_TEMPLATE.formatted(id);
                log.error(message);
                return new ResourceNotFoundException(message);
            }
        );

        log.info("Found card: {}", card);
        return card;
    }

    @Override
    public List<Card> findByUserId(final Long userId) {
        throw new NotImplementedException();
    }

    @Override
    @Transactional
    public Card create(final CardRequestDto card) {
        log.debug("Attempting create card for user: {}", card.userEmail());
        byte[] encrypted = {};
        try {
            encrypted = encryptionService.encrypt(card.cardNumber());
        } catch (Exception e) {
            throw new BusinessException(e);
        }
        var masked = mask(card.cardNumber());

        var newCard = Card.builder()
            .cardNumber(encrypted)
            .maskedCardNumber(masked)
            .cardHolderName(card.userEmail())
            .balance(BigDecimal.valueOf(0))
            .status(CardStatus.ACTIVE)
            .user(this.userService.findByEmail(card.userEmail()))
            .expirationDate(LocalDate.now().plusYears(5))
            .build();

        var saved = this.cardRepository.save(newCard);

        log.info("Created card: {}", newCard);
        return saved;
    }

    @Override
    public Card findByCardIdAndUserEmail(final Long cardId, final String email) {
        log.debug("Attempting find card by id: {} and email: {}", cardId, email);
        var card = this.cardRepository.findByIdAndCardHolderName(cardId, email).orElseThrow(
            () -> {
                var message = CARD_WITH_ID_AND_USER_EMAIL_WAS_NOT_FOUND_TEMPLATE.formatted(cardId, email);
                log.error(message);
                return new ResourceNotFoundException(message);
            }
        );

        log.info("Found card: {}", card);
        return card;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardWithUserDto> findAll() {
        log.debug("Attempting find all cards");

        var cards = this.cardRepository.findAllCardsWithUsers();

        log.info("Found {} cards", cards);
        return cards;
    }

    @Override
    @Transactional
    public Card changeStatus(final Long id, final CardStatus status) {
        log.debug("Attempting change status for id: {}", id);

        var card = this.findById(id);
        card.setStatus(status);

        var updated = this.cardRepository.save(card);

        log.info("Updated card: {}", updated);
        return updated;
    }

    @Override
    @Transactional
    public void delete(final Long id) {
        log.debug("Attempting delete card with id: {}", id);

        var card = this.findById(id);

        this.cardRepository.delete(card);

        log.info("Deleted card: {}", card);
    }

    @Override
    @Transactional
    public Card saveLimit(final Long id, final CardLimit cardLimit) {
        log.debug("Attempting save limit for id: {}", id);

        var card = this.findById(id);
        card.setLimit(cardLimit);

        var updated = this.cardRepository.save(card);

        log.info("Updated card: {}", updated);
        return updated;
    }

    @Override
    @Transactional
    public void updateAllBalances(final List<Card> cards) {
        log.debug("Attempting update all balances for cards: {}", cards);

        for (var card : cards) {
            var candidate = this.findById(card.getId());
            candidate.setBalance(card.getBalance());
            this.cardRepository.save(candidate);
        }

        log.info("Updated all balances");
    }

    @Override
    @Transactional
    public void deleteLimit(final Long cardId) {
        log.debug("Attempting delete limit for id: {}", cardId);

        var card = this.findById(cardId);
        card.setLimit(null);

        var updated = this.cardRepository.save(card);

        log.info("Updated card: {}", updated);
    }

    @Override
    public boolean existsByUserEmailAndCardId(final String userEmail, final Long cardId) {
        log.debug("Attempting exists by user email '{}' and card id {}", userEmail, cardId);

        var isExists = this.cardRepository.existsByIdAndUserEmail(cardId, userEmail);

        log.info("Exists card: {}", isExists);
        return isExists;
    }

    @Override
    @Transactional
    public Card setStatus(Long id, final CardStatus cardStatus) {
        log.debug("Attempting set status for id: {}", id);

        var card = this.findById(id);
        card.setStatus(cardStatus);

        var updated = this.cardRepository.save(card);

        log.info("Updated card: {}", updated);
        return updated;
    }

    private String mask(final String cardNumber) {
        var last4 = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + last4;
    }
}
