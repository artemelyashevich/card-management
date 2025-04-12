package com.elyashevich.card_manager.api.controller;

import com.elyashevich.card_manager.api.dto.auth.AuthDto;
import com.elyashevich.card_manager.api.dto.user.UserRequestDto;
import com.elyashevich.card_manager.api.mapper.UserMapper;
import com.elyashevich.card_manager.service.AuthService;
import com.elyashevich.card_manager.util.AuthDtoGeneratorUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private static final UserMapper userMapper = UserMapper.INSTANCE;

    @PostMapping("/login")
    public ResponseEntity<AuthDto> login(
        final @RequestBody @Valid UserRequestDto userRequestDto,
        final UriComponentsBuilder uriComponentsBuilder
        ) {
        var result = this.authService.authenticate(userMapper.toEntity(userRequestDto));
        var authDto = AuthDtoGeneratorUtil.generateAuthDto(result);
        return ResponseEntity.created(
            uriComponentsBuilder
                .replacePath("/api/v1/users/{email}")
                .build(Map.of("email", result.getEmail()))
        ).body(authDto);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDto> register(
        final @RequestBody @Valid UserRequestDto userRequestDto,
        final UriComponentsBuilder uriComponentsBuilder
    ) {
        var result = this.authService.register(userMapper.toEntity(userRequestDto));
        var authDto = AuthDtoGeneratorUtil.generateAuthDto(result);
        return ResponseEntity.created(
            uriComponentsBuilder
                .replacePath("/api/v1/users/{email}")
                .build(Map.of("email", result.getEmail()))
        ).body(authDto);
    }

    @PostMapping("/refresh/{token}")
    public ResponseEntity<AuthDto> refresh(final @PathVariable("token") String token) {
        var result = this.authService.refresh(token);
        return ResponseEntity.ok(AuthDtoGeneratorUtil.generateAuthDto(result));
    }
}
