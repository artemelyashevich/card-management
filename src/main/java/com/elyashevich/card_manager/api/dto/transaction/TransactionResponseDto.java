package com.elyashevich.card_manager.api.dto.transaction;

import com.elyashevich.card_manager.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponseDto(
    Long id,
    BigDecimal amount,
    LocalDateTime timestamp,
    TransactionType type,
    String description
) {
}
