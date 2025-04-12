package com.elyashevich.card_manager.util;

import com.elyashevich.card_manager.api.dto.auth.AuthDto;
import com.elyashevich.card_manager.entity.User;
import lombok.experimental.UtilityClass;

import static com.elyashevich.card_manager.util.TokenConstantUtil.ACCESS_TOKEN_EXPIRES_TIME;
import static com.elyashevich.card_manager.util.TokenConstantUtil.REFRESH_TOKEN_EXPIRES_TIME;

@UtilityClass
public class AuthDtoGeneratorUtil {

    public static AuthDto generateAuthDto(User user) {
        var accessToken = TokenUtil.generateToken(user, ACCESS_TOKEN_EXPIRES_TIME);
        var refreshToken = TokenUtil.generateToken(user, REFRESH_TOKEN_EXPIRES_TIME);
        return new AuthDto(accessToken, refreshToken);
    }
}
