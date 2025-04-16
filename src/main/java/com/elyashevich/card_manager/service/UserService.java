package com.elyashevich.card_manager.service;

import com.elyashevich.card_manager.entity.User;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User findById(final Long id);

    User findByEmail(final String email);

    User create(final User user);

    User updateEmail(final Long id, final User user);

    void delete(final Long id);
}
