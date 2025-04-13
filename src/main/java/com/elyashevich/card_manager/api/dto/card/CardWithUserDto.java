package com.elyashevich.card_manager.api.dto.card;

import com.elyashevich.card_manager.entity.CardStatus;
import com.elyashevich.card_manager.entity.Role;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface CardWithUserDto {
    Long getId();
    String getMaskedCardNumber();
    String getCardHolderName();
    UserDto getUser();
    BigDecimal getBalance();
    LocalDate getExpirationDate();
    CardStatus getStatus();

    interface UserDto {
        Long getId();
        String getEmail();
        List<Role> getRoles();
    }
}