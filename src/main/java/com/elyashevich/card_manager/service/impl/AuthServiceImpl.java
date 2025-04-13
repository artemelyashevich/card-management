package com.elyashevich.card_manager.service.impl;

import com.elyashevich.card_manager.entity.User;
import com.elyashevich.card_manager.exception.PasswordMismatchException;
import com.elyashevich.card_manager.service.AuthService;
import com.elyashevich.card_manager.service.UserService;
import com.elyashevich.card_manager.util.SafetyExtractEmailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public User authenticate(final User user) {
        log.debug("Attempting to authenticate user: {}", user);

        var candidate = this.userService.findByEmail(user.getEmail());
        if (!this.passwordEncoder.matches(user.getPassword(), candidate.getPassword())) {
            log.error("Invalid password");
            throw new PasswordMismatchException("Invalid password");
        }

        log.info("User authenticated: {}", user);
        return candidate;
    }

    @Override
    public User register(final User user) {
        log.debug("Attempting to register user: {}", user);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        var newUser = this.userService.create(user);

        this.auth(newUser);

        log.info("Registered user: {}", newUser);
        return newUser;
    }

    @Override
    public User refresh(final String token) {
        log.debug("Attempting to refresh user: {}", token);

        var user = this.userService.findByEmail(SafetyExtractEmailUtil.extractEmailClaims(token));

        this.auth(user);

        log.info("Refreshed user: {}", user);
        return user;
    }

    private void auth(final User user) {
        var token = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        this.authenticationManager.authenticate(token);
    }
}
