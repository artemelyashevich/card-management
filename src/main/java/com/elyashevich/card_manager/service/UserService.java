package com.elyashevich.card_manager.service;

import com.elyashevich.card_manager.api.dto.filter.FilterDto;
import com.elyashevich.card_manager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface UserService {

    Page<User> findAll(final FilterDto filterDto, final PageRequest pageRequest);

    User findById(final Long id);

    User findByEmail(final String email);

    User create(final User user);

    User updateEmail(final Long id, final User user);

    void delete(final Long id);
}
