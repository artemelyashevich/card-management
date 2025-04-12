package com.elyashevich.card_manager.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TokenConstantUtil {

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final Long ACCESS_TOKEN_EXPIRES_TIME = 1800000L; // 30m
    public static final Long REFRESH_TOKEN_EXPIRES_TIME = 864000000L; // 10d
}