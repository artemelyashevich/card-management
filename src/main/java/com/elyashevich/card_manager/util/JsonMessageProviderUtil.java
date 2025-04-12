package com.elyashevich.card_manager.util;

import com.elyashevich.card_manager.api.dto.exception.ExceptionBodyDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.io.IOException;

@UtilityClass
public class JsonMessageProviderUtil {

    public static String provide(String message) throws IOException {
        var objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(new ExceptionBodyDto(message));
    }
}
