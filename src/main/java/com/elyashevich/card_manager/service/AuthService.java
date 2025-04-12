package com.elyashevich.card_manager.service;

import com.elyashevich.card_manager.api.dto.auth.AuthDto;
import com.elyashevich.card_manager.entity.User;

public interface AuthService {

    User authenticate(final User user);

    User register(final User user);

    User refresh(final String token);
}
