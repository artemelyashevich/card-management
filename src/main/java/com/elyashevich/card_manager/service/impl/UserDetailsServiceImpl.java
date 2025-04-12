package com.elyashevich.card_manager.service.impl;

import com.elyashevich.card_manager.entity.Role;
import com.elyashevich.card_manager.exception.ResourceNotFoundException;
import com.elyashevich.card_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        var user = this.userRepository.findByEmail(username).orElseThrow(() -> {
            var message = "User with email '%s' was not found".formatted(username);
            log.error(message);
            return new ResourceNotFoundException(message);
        });
        return new User(
            user.getEmail(),
            user.getPassword(),
            user.getRoles().stream()
                .map(Role::name)
                .map(SimpleGrantedAuthority::new)
                .toList()
        );
    }
}
