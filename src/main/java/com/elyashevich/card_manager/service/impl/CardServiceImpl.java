package com.elyashevich.card_manager.service.impl;

import com.elyashevich.card_manager.api.dto.card.CardRequestDto;
import com.elyashevich.card_manager.api.dto.card.CardWithUserDto;
import com.elyashevich.card_manager.entity.Card;
import com.elyashevich.card_manager.entity.CardStatus;
import com.elyashevich.card_manager.exception.ResourceNotFoundException;
import com.elyashevich.card_manager.repository.CardRepository;
import com.elyashevich.card_manager.service.CardService;
import com.elyashevich.card_manager.service.EncryptionService;
import com.elyashevich.card_manager.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    public static final String CARD_WITH_ID_WAS_NOT_FOUND_TEMPLATE = "Card with id: '%d' was not found";
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
            throw new RuntimeException(e);
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
    public List<CardWithUserDto> findAll() {
        log.debug("Attempting find all cards");

        var cards = this.cardRepository.findAllCardsWithUsers();

        log.info("Found {} cards", cards);
        return cards;
    }

    private String mask(final String cardNumber) {
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + last4;
    }
}
