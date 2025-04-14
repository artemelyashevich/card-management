package com.elyashevich.card_manager.api.controller;

import com.elyashevich.card_manager.api.dto.transaction.TransactionResponseDto;
import com.elyashevich.card_manager.api.dto.transaction.TransferRequestDto;
import com.elyashevich.card_manager.api.dto.transaction.WithdrawRequestDto;
import com.elyashevich.card_manager.api.mapper.TransactionMapper;
import com.elyashevich.card_manager.entity.Transaction;
import com.elyashevich.card_manager.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private static final TransactionMapper transactionMapper = TransactionMapper.INSTANCE;

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> findTransactionById(final @PathVariable("id") Long id) {
        var transaction = this.transactionService.findById(id);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/card/{id}")
    public ResponseEntity<List<TransactionResponseDto>> findTransactionByCardId(final @PathVariable("id") Long id) {
        var transactions = this.transactionService.findAllByCardId(id);
        return ResponseEntity.ok(transactionMapper.toDto(transactions));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDto> transfer(
        final @RequestBody TransferRequestDto transferRequest,
        final UriComponentsBuilder uriComponentsBuilder
    ) {
        var transaction = this.transactionService.transferBetweenCards(
            transferRequest.fromCardId(),
            transferRequest.toCardId(),
            transferRequest.amount()
        );
        return ResponseEntity.created(
            uriComponentsBuilder.replacePath("/api/v1/transactions/{id}")
                .build(Map.of("id", transaction.getId()))
        ).body(transactionMapper.toDto(transaction));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponseDto> withdrawFromCard(
        final @RequestBody WithdrawRequestDto request,
        final UriComponentsBuilder uriComponentsBuilder
    ) {
        var transaction = this.transactionService.withdrawFromCard(request.cardId(), request.amount());
        return ResponseEntity.created(
            uriComponentsBuilder.replacePath("/api/v1/transactions/{id}")
                .build(Map.of("id", transaction.getId()))
        ).body(transactionMapper.toDto(transaction));
    }
}
