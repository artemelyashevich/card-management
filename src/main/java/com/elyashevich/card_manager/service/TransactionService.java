package com.elyashevich.card_manager.service;

import com.elyashevich.card_manager.entity.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {

    List<Transaction> findAllByCardId(final Long cardId);

    Transaction transferBetweenCards(final Long fromCardId, final Long toCardId, final BigDecimal amount);

    Transaction withdrawFromCard(final Long cardId, final BigDecimal amount);

    Transaction findById(final Long id);
}
