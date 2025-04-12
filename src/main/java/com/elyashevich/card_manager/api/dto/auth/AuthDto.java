package com.elyashevich.card_manager.api.dto.auth;

public record AuthDto(
    String accessToken,
    String refreshToken
) {
}
