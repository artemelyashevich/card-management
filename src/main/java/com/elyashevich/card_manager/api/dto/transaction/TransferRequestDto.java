package com.elyashevich.card_manager.api.dto.transaction;

import java.math.BigDecimal;

public record TransferRequestDto(
    Long fromCardId,
    Long toCardId,
    BigDecimal amount
) {
}
