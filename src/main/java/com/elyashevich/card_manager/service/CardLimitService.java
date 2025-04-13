package com.elyashevich.card_manager.service;

import com.elyashevich.card_manager.entity.Card;
import com.elyashevich.card_manager.entity.CardLimit;

public interface CardLimitService {

    Card setLimit(final Long cardId, final CardLimit limit);

    void deleteLimit(final Long cardId, final Long limitId);
}
