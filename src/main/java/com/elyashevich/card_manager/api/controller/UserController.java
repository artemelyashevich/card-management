package com.elyashevich.card_manager.api.controller;

import com.elyashevich.card_manager.api.dto.filter.FilterDto;
import com.elyashevich.card_manager.api.dto.user.UserResponseDto;
import com.elyashevich.card_manager.api.mapper.UserMapper;
import com.elyashevich.card_manager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private static final UserMapper userMapper = UserMapper.INSTANCE;

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> findAll() {
        var users = this.userService.findAll();

        return ResponseEntity.ok(userMapper.toDto(users));
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserResponseDto> findByEmail(final @PathVariable("email") String email) {
        var user = this.userService.findByEmail(email);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(final @PathVariable("id") Long id) {
        this.userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
