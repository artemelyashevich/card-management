package com.elyashevich.card_manager.api.controller;

import com.elyashevich.card_manager.api.dto.card.CardRequestDto;
import com.elyashevich.card_manager.api.dto.card.CardWithUserDto;
import com.elyashevich.card_manager.entity.Card;
import com.elyashevich.card_manager.entity.CardStatus;
import com.elyashevich.card_manager.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping
    public ResponseEntity<List<CardWithUserDto>> findAll() {
        var cards = this.cardService.findAll();
        return ResponseEntity.ok(cards);
    }

    @PostMapping
    public ResponseEntity<Card> save(
        final @RequestBody @Valid CardRequestDto cardDto,
        final UriComponentsBuilder uriComponentsBuilder
    ) {
        var card = this.cardService.create(cardDto);

        return ResponseEntity.created(
            uriComponentsBuilder
                .replacePath("/api/v1/cards/{cardId}")
                .build(Map.of("cardId", card.getId()))
        ).body(card);
    }

    @PostMapping("/block/{id}")
    public ResponseEntity<Card> block(
        final @PathVariable Long id,
        final UriComponentsBuilder uriComponentsBuilder
    ) {
        var card = this.cardService.setStatus(id, CardStatus.BLOCKED);
        return ResponseEntity.created(
            uriComponentsBuilder.replacePath("/api/v1/cards/{id}")
                .build(Map.of("id", card.getId()))
        ).body(card);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(final @PathVariable("id") Long id) {
        this.cardService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
