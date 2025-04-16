package com.elyashevich.card_manager.repository;

import com.elyashevich.card_manager.api.dto.card.CardWithUserDto;
import com.elyashevich.card_manager.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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
        ORDER BY :sortField
        LIMIT :pageSize
        OFFSET :skip
        """)
    List<CardWithUserDto> findAllCardsWithUsers(
        final @Param("sortField") String sortField,
        final @Param("pageSize") int pageSize,
        final @Param("skip") int skip
        );

    @Query(
        """
        SELECT
            c.id as id,
            c.maskedCardNumber as maskedCardNumber,
            c.cardHolderName as cardHolderName,
            c.user as user,
            c.balance as balance,
            c.expirationDate as expirationDate,
            c.status as status
        FROM Card c
        WHERE id = :id
        """
    )
    Optional<CardWithUserDto> findCardWithUserById(final @Param("id") Long id);

    boolean existsByIdAndUserEmail(final Long cardId, final String userEmail);

    Optional<Card> findByIdAndCardHolderName(final Long cardId, final String cardHolderName);

    @Query(
        """
        SELECT
            c.id as id,
            c.maskedCardNumber as maskedCardNumber,
            c.cardHolderName as cardHolderName,
            c.user as user,
            c.balance as balance,
            c.expirationDate as expirationDate,
            c.status as status
        FROM Card c
        WHERE c.user.id = :userId
        """
    )
    List<CardWithUserDto> findAllCardWithUserById(final @Param("userId") Long userId);
}
