package com.elyashevich.card_manager.api.dto.limit;

import java.math.BigDecimal;

public record CardLimitRequestDto(
    BigDecimal dailyLimit,

    BigDecimal monthlyLimit
) {
}
