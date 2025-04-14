package com.elyashevich.card_manager.api.dto.transaction;

import java.math.BigDecimal;

public record WithdrawRequestDto(
    Long cardId,
    BigDecimal amount
) {
}
