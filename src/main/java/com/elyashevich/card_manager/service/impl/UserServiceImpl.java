package com.elyashevich.card_manager.service.impl;

import com.elyashevich.card_manager.entity.Role;
import com.elyashevich.card_manager.entity.User;
import com.elyashevich.card_manager.exception.ResourceAlreadyExistsException;
import com.elyashevich.card_manager.exception.ResourceNotFoundException;
import com.elyashevich.card_manager.repository.UserRepository;
import com.elyashevich.card_manager.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public static final String USER_WITH_ID_WAS_NOT_FOUND_TEMPLATE = "User with id: '%d' was not found";
    public static final String USER_WITH_EMAIL_WAS_NOT_FOUND_TEMPLATE = "User with email: '%s' was not found.";
    public static final String USER_WITH_EMAIL_ALREADY_EXISTS_TEMPLATE = "User with email: '%s' already exists";

    private final UserRepository userRepository;

    @Override
    @Cacheable(value = "UserServiceImpl::findAll")
    public List<User> findAll() {
        log.debug("Attempting to find all users");

        var users = userRepository.findAll();

        log.info("Found users: {}", users);
        return users;
    }

    @Override
    public User findById(final Long id) {
        log.debug("Attempting to find user by id: {}", id);

        var user = this.userRepository.findById(id).orElseThrow(
            () -> {
                var message = USER_WITH_ID_WAS_NOT_FOUND_TEMPLATE.formatted(id);
                log.error(message);
                return new ResourceNotFoundException(message);
            }
        );

        log.info("User found: {}", user);
        return user;
    }

    @Override
    @Cacheable(value = "UserServiceImpl::findByEmail", key = "#email")
    public User findByEmail(final String email) {
        log.debug("Attempting to find user by email: {}", email);

        var user = this.userRepository.findByEmail(email).orElseThrow(
            () -> {
                var message = USER_WITH_EMAIL_WAS_NOT_FOUND_TEMPLATE.formatted(email);
                log.error(message);
                return new ResourceNotFoundException(message);
            }
        );

        log.info("User found: {}", user);
        return user;
    }

    @Override
    @Transactional
    @Caching(
        put = {
            @CachePut(value = "UserServiceImpl::findById", key="#result.id"),
            @CachePut(value = "UserServiceImpl::findByEmail", key = "#user.email"),
            @CachePut(value = "UserServiceImpl::findAll")
        }
    )
    public User create(final User user) {
        log.debug("Attempting to create user: {}", user);

        if (this.userRepository.existsByEmail(user.getEmail())) {
            var message = USER_WITH_EMAIL_ALREADY_EXISTS_TEMPLATE.formatted(user.getEmail());
            log.error(message);
            throw new ResourceAlreadyExistsException(message);
        }

        user.addRole(Role.ROLE_USER);

        var newUser = this.userRepository.save(user);

        log.info("User created: {}", newUser);
        return newUser;
    }

    @Override
    @Transactional
    @Caching(
        put = {
            @CachePut(value = "UserServiceImpl::findById", key="#result.id"),
            @CachePut(value = "UserServiceImpl::findByEmail", key = "#user.email"),
            @CachePut(value = "UserServiceImpl::findAll")
        }
    )
    public User updateEmail(final Long id, final User user) {
        log.debug("Attempting to update user with id: {}", id);

        var oldUser = this.findById(id);

        if (!oldUser.getEmail().equals(user.getEmail())) {
            var existsWithSameEmail = this.userRepository.existsByEmail(user.getEmail());
            if (existsWithSameEmail) {
                var message = USER_WITH_EMAIL_ALREADY_EXISTS_TEMPLATE.formatted(user.getEmail());
                log.error(message);
                throw new ResourceAlreadyExistsException(message);
            }
        }

        oldUser.setEmail(user.getEmail());

        var newUser = this.userRepository.save(oldUser);

        log.info("User updated: {}", oldUser);
        return newUser;
    }

    @Override
    @Transactional
    @CacheEvict(value = "UserServiceImpl::findById", key = "#id")
    public void delete(final Long id) {
        log.debug("Attempting to delete user by id: {}", id);

        var candidate = this.findById(id);

        this.userRepository.delete(candidate);

        log.info("User deleted: {}", candidate);
    }
}
