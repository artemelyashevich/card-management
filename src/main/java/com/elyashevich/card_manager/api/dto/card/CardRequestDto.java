package com.elyashevich.card_manager.api.dto.card;

public record CardRequestDto(
    String cardNumber,
    String userEmail
) {
}
