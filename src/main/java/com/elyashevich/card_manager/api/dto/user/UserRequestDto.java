package com.elyashevich.card_manager.api.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record UserRequestDto(

    @NotNull(message = "Email must be not null")
    @NotBlank(message = "Email must be not empty")
    @Email(message = "Invalid email format")
    String email,

    @NotNull(message = "Password must be not null")
    @NotBlank(message = "Password must be not empty")
    @Length(min = 8, max = 255, message = "Password must be in {min} and {max}")
    String password
) {
}
