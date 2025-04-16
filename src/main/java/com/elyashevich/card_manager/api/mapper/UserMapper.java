package com.elyashevich.card_manager.api.mapper;

import com.elyashevich.card_manager.api.dto.user.UserRequestDto;
import com.elyashevich.card_manager.api.dto.user.UserResponseDto;
import com.elyashevich.card_manager.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserResponseDto toDto(final User user);

    List<UserResponseDto> toDto(final List<User> users);

    User toEntity(final UserRequestDto userResponseDto);
}
