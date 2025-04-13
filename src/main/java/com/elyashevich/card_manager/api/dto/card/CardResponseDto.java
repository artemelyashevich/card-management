package com.elyashevich.card_manager.api.dto.card;

import com.elyashevich.card_manager.api.dto.user.UserResponseDto;
import com.elyashevich.card_manager.entity.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CardResponseDto(
    Long id,
    BigDecimal balance,
    String maskedCardNumber,
    String cardHolderName,
    LocalDate expirationDate,
    CardStatus status,
    UserResponseDto user
) {
}
