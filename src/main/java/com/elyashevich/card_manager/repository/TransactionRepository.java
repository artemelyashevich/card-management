package com.elyashevich.card_manager.repository;

import com.elyashevich.card_manager.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(
        """
        SELECT COALESE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.card.id = :cardId
            AND DATE(t.timestamp) = :date
            AND t.type = 'WITHDRAWAL'
        """
    )
    BigDecimal calculateDailySpent(
        final @Param("cardId") Long cardId,
        final @Param("date") LocalDate date
    );

    @Query(
        """
        SELECT COALESE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.card.id = :cardId
            AND YEAR(t.timestamp) = :year
            AND MONTH(t.timestamp) = :month
            AND t.type = 'WITHDRAWAL'
        """
    )
    BigDecimal calculateMonthlySpent(
        final @Param("cardId") Long cardId,
        final @Param("year") Integer year,
        final @Param("month") Integer month
    );

    List<Transaction> findAllByCardId(final Long cardId);
}
