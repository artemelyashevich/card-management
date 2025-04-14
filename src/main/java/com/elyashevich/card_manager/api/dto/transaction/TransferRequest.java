package com.elyashevich.card_manager.api.dto.transaction;

import java.math.BigDecimal;

public record TransferRequest(
    Long fromCardId,
    Long toCardId,
    BigDecimal amount
) {
}
