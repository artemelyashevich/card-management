package com.elyashevich.card_manager.api.controller;

import com.elyashevich.card_manager.api.dto.limit.CardLimitRequestDto;
import com.elyashevich.card_manager.api.mapper.CardLimitMapper;
import com.elyashevich.card_manager.entity.Card;
import com.elyashevich.card_manager.service.CardLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/limits")
@RequiredArgsConstructor
public class LimitController {

    private final CardLimitService cardLimitService;
    private static final CardLimitMapper cardLimitMapper = CardLimitMapper.INSTANCE;

    @PostMapping("/{cardId}")
    public ResponseEntity<Card> setLimit(
        final @PathVariable("cardId") Long cardId,
        final @RequestBody CardLimitRequestDto dto,
        final UriComponentsBuilder uriComponentsBuilder
    ) {
        var card = this.cardLimitService.setLimit(cardId, cardLimitMapper.toEntity(dto));
        return ResponseEntity.created(
            uriComponentsBuilder.replacePath("/api/v1/cards/{cardId}")
                .build(Map.of("cartdId", cardId))
        ).body(card);
    }

    @DeleteMapping("/{cardId}/{limitId}")
    public ResponseEntity<Void> deleteLimit(
        final @PathVariable("cardId") Long cardId,
        final @PathVariable("limitId") Long limitId
    ) {
        this.cardLimitService.deleteLimit(cardId, limitId);
        return ResponseEntity.noContent().build();
    }
}
