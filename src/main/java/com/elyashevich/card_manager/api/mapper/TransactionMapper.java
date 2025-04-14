package com.elyashevich.card_manager.api.mapper;

import com.elyashevich.card_manager.api.dto.transaction.TransactionResponseDto;
import com.elyashevich.card_manager.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface TransactionMapper {

    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    TransactionResponseDto toDto(final Transaction transaction);

    List<TransactionResponseDto> toDto(final List<Transaction> transactions);
}
