package com.elyashevich.card_manager.api.dto.transaction;

import java.math.BigDecimal;

public record WithdrawRequest(
    Long cardId,
    BigDecimal amount
) {
}
