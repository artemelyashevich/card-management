package com.elyashevich.card_manager.service;

import com.elyashevich.card_manager.api.dto.card.CardRequestDto;
import com.elyashevich.card_manager.api.dto.card.CardWithUserDto;
import com.elyashevich.card_manager.entity.Card;

import java.util.List;

public interface CardService {

    Card findById(final Long id);

    List<Card> findByUserId(final Long userId);

    Card create(final CardRequestDto card);

    List<CardWithUserDto> findAll();
}
