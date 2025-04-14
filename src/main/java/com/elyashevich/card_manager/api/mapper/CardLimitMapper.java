package com.elyashevich.card_manager.api.mapper;

import com.elyashevich.card_manager.api.dto.limit.CardLimitRequestDto;
import com.elyashevich.card_manager.entity.CardLimit;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface CardLimitMapper {

    CardLimitMapper INSTANCE = Mappers.getMapper(CardLimitMapper.class);

    CardLimit toEntity(final CardLimitRequestDto dto);
}
