package com.elyashevich.card_manager.repository;

import com.elyashevich.card_manager.api.dto.card.CardWithUserDto;
import com.elyashevich.card_manager.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    @Query("""
    SELECT 
        c.id as id,
        c.maskedCardNumber as maskedCardNumber,
        c.cardHolderName as cardHolderName,
        c.user as user,
        c.balance as balance,
        c.expirationDate as expirationDate,
        c.status as status
    FROM Card c
    """)
    List<CardWithUserDto> findAllCardsWithUsers();
}
