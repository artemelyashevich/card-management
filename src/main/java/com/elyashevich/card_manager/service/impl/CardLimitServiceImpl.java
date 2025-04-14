package com.elyashevich.card_manager.service.impl;

import com.elyashevich.card_manager.entity.Card;
import com.elyashevich.card_manager.entity.CardLimit;
import com.elyashevich.card_manager.exception.ResourceNotFoundException;
import com.elyashevich.card_manager.repository.CardLimitRepository;
import com.elyashevich.card_manager.service.CardLimitService;
import com.elyashevich.card_manager.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardLimitServiceImpl implements CardLimitService {

    public static final String CARD_LIMIT_WITH_ID_WAS_NOT_FOUND_TEMPLATE = "Card limit with id: '%d' was not found";
    private final CardService cardService;
    private final CardLimitRepository cardLimitRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Card setLimit(final Long cardId, final CardLimit limit) {
        log.debug("Attempting set limit to card: {}", cardId);

        var cardLimit = this.cardLimitRepository.save(limit);
        var updatedCard = this.cardService.saveLimit(cardId, cardLimit);

        log.debug("Limit updated: {}", updatedCard);
        return updatedCard;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteLimit(final Long cardId, final Long limitId) {
        log.debug("Attempting delete limit to card: {}", cardId);

        var cardLimit = this.cardLimitRepository.findById(cardId).orElseThrow(
            () -> {
                var message = CARD_LIMIT_WITH_ID_WAS_NOT_FOUND_TEMPLATE.formatted(limitId);
                log.error(message);
                return new ResourceNotFoundException(message);
            }
        );

        this.cardService.deleteLimit(cardId);
        this.cardLimitRepository.delete(cardLimit);

        log.info("Limit deleted: {}", cardId);
    }
}
