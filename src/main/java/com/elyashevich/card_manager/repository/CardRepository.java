package com.elyashevich.card_manager.repository;

import com.elyashevich.card_manager.api.dto.card.CardWithUserDto;
import com.elyashevich.card_manager.entity.Card;
import com.elyashevich.card_manager.entity.User;
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
        ORDER BY :sortField :sortDir
        LIMIT
          :pageSize
        OFFSET
          (:pageNo - 1) * pageSize
        """)
    List<CardWithUserDto> findAllCardsWithUsers(
        final @Param("sortDir") String sort,
        final @Param("sortField") String sortField,
        final @Param("pageNo") int pageNo,
        final @Param("pageSize") int pageSize
        );

    List<Card> user(final User user);

    boolean existsByIdAndUserEmail(final Long cardId, final String userEmail);

    Optional<Card> findByIdAndCardHolderName(final Long cardId, final String cardHolderName);
}
