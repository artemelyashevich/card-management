package com.elyashevich.card_manager.api.dto.user;

import com.elyashevich.card_manager.entity.Role;

import java.util.List;

public record UserResponseDto(
    Long id,
    String email,
    List<Role> roles
) {
}
