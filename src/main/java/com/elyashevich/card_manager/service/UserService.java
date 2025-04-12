package com.elyashevich.card_manager.service;

import com.elyashevich.card_manager.entity.User;

public interface UserService {

    User findById(final Long id);

    User findByEmail(final String email);

    User create(final User user);

    User updateEmail(final Long id, final User user);

    void delete(final Long id);
}
